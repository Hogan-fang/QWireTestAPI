# QWireTestAPI 需求说明

## 1. 测试工程定位

ISO/IEC 25010 将软件质量划分为功能适用性（Functional Suitability）、性能效率（Performance Efficiency）、兼容性（Compatibility）、可靠性（Reliability）、安全性（Security）等多个质量特性，为软件质量评价提供了统一的参考模型。然而，不同类型的测试工程通常只覆盖其中的部分质量特性，而并非所有质量属性都由同一种测试方法完成。

本 API 测试工程以功能测试（Functional Testing）为主要目标，通过自动化测试框架验证系统接口是否按照业务需求正确实现，并验证系统在不同业务场景下的数据处理、状态流转及接口交互行为。

在功能测试过程中，除了验证接口功能本身外，还会涉及部分与兼容性（Compatibility）和安全性（Security）相关的功能要求。例如，多商户数据隔离、订单唯一性校验、接口签名验证以及敏感信息脱敏等，这些均属于接口业务规范的一部分，其验证目标是确认系统是否正确实现了相应的业务规则，而非对系统进行渗透测试、漏洞扫描或密码学安全验证。

因此，本工程所涉及的安全相关测试，应理解为功能需求中的安全性验证（Security-related Functional Verification），而不是广义的软件安全测试。

## 2. ISO/IEC 25010 质量特性覆盖范围

| ISO 25010 质量特性 | 本工程涉及程度 | 示例 |
|---|---|---|
| **Functional Suitability** | ★★★★★（核心） | 支付、查询、状态流转、异常处理 |
| **Compatibility** | ★★☆☆☆（部分） | 多商户共存、接口交互 |
| **Security** | ★★☆☆☆（功能层面） | 数据隔离、脱敏、签名校验 |
| Reliability | ★☆☆☆☆（少量） | 超时、重复回调等可作为异常场景 |
| Performance Efficiency | ☆☆☆☆☆ | 不属于本工程 |
| Maintainability | ☆☆☆☆☆ | 属于框架设计，不属于测试工程 |
| Portability | ☆☆☆☆☆ | 不属于测试工程 |

## 3. 测试场景范围

本工程主要围绕以下场景展开：

- 正常业务场景（Normal）：验证接口能够正确完成业务处理。
- 边界场景（Boundary）：验证系统在边界数据条件下的处理行为。
- 异常场景（Exception）：验证非法输入及异常请求的处理结果。
- 业务流程场景（Workflow）：验证支付、查询、状态流转及回调等完整业务流程。
- 功能性安全验证（Security）：验证多商户数据隔离、敏感数据脱敏、签名校验等功能性安全要求。

通过这些测试场景，全面展示 QWire 自动化测试框架在 HTTP 通信、JSON 响应检查、数据库检查、回调检查以及流程编排等方面的能力，并验证框架在 API 功能测试中的工程化应用。

## 4. 测试场景设计

主要方法：将测试框架的能力以及功能说明提供给 AI，引导 AI 检测测试矩阵以及测试场景设计。

### 4.1 场景矩阵

| 接口/流程 | 正常交易 | 边界 | 异常 | 安全性 |
|---|---|---|---|---|
| 支付 Create Order | 支付成功<br>支付失败<br>业务失败入库 | 金额边界<br>商品金额汇总<br>Reference 长度<br>币种大小写 | 参数缺失<br>格式错误<br>重复订单<br>非法卡号 | 签名 Header<br>卡号脱敏<br>不保存 CVV/expiry |
| 查询 Query Order | 按 reference 查询<br>按 orderId 查询 | 不同 merchant 隔离<br>订单状态边界 | 订单不存在<br>merchant 不匹配<br>查询参数缺失 | 不返回 CVV/expiry/cardholderName |
| 支付 + 查询 | 支付后立即查询<br>支付后回调<br>30 秒后履约完成 | 支付成功但履约未完成<br>履约完成后再查 | 支付失败后查询<br>重复支付后查询 | 全流程数据脱敏<br>跨 merchant 不可查 |

## 5. 支付接口（Create Order）

### 5.1 正常交易（Normal）

| 编号 | 测试场景 | 关键验证点 | 测试数据特殊点 |
|---|---|---|---|
| PAY-N-001 | 支付成功 | HTTP 200<br>paymentStatus=PAID<br>生成 orderId 并写入数据库 | reference 唯一<br>正常 Merchant<br>正常金额<br>成功卡号 |
| PAY-N-002 | 支付业务失败 | HTTP 200<br>paymentStatus=FAILED<br>返回 failReason<br>订单写入数据库 | Mock 失败卡号<br>reference 唯一 |
| PAY-N-003 | 多商户支付 | 两个 Merchant 均创建成功且互不影响 | merchantId 不同<br>reference 可相同 |

### 5.2 边界测试（Boundary）

| 编号 | 测试场景 | 关键验证点 | 测试数据特殊点 |
|---|---|---|---|
| PAY-B-001 | 最小金额 | 创建成功<br>金额正确 | amount=0.01 |
| PAY-B-002 | 最大金额 | 创建成功<br>金额正确 | 使用系统允许最大金额 |
| PAY-B-003 | Reference 最短长度 | 创建成功 | 最短合法 reference |
| PAY-B-004 | Reference 最长长度 | 创建成功 | 最长合法 reference |
| PAY-B-005 | 多商品订单 | 商品金额汇总正确<br>订单金额正确 | 多个 products<br>总金额一致 |

### 5.3 异常测试（Exception）

| 编号 | 测试场景 | 关键验证点 | 测试数据特殊点 |
|---|---|---|---|
| PAY-E-001 | merchant 缺失 | HTTP 422<br>不写数据库 | 删除 merchantId |
| PAY-E-002 | amount 负数 | HTTP 400 | amount=-1 |
| PAY-E-003 | 非法币种 | HTTP 400 | currency=XXX |
| PAY-E-004 | 非法卡号 | HTTP 400 | 非法 cardNumber |
| PAY-E-005 | 重复订单 | HTTP 409 | 同 merchant<br>同 reference |
| PAY-E-006 | JSON 格式错误 | HTTP 422 | amount 类型错误 |

### 5.4 安全测试（Security）

| 编号 | 测试场景 | 关键验证点 | 测试数据特殊点 |
|---|---|---|---|
| PAY-S-001 | 签名正确 | 请求正常处理 | 正确 Header |
| PAY-S-002 | 缺少签名 | 请求拒绝 | 删除 Header |
| PAY-S-003 | 错误签名 | 请求拒绝 | 错误 Header |
| PAY-S-004 | 数据库存储脱敏 | cardNumber 脱敏<br>CVV 不存在<br>Expiry 不存在 | 特殊尾号卡号<br>特殊 CVV |
| PAY-S-005 | 响应无敏感信息 | response 无 CVV<br>无 Expiry<br>无 Cardholder | 使用完整敏感数据提交 |

## 6. 查询接口（Query Order）

| 编号 | 测试场景 | 关键验证点 | 测试数据特殊点 |
|---|---|---|---|
| QUERY-N-001 | Reference 查询成功 | 查询成功<br>返回最新状态 | 创建订单后查询 |
| QUERY-N-002 | OrderId 查询成功 | 查询成功<br>返回最新状态 | 保存 orderId |
| QUERY-B-001 | 同 Reference 不同 Merchant | 数据隔离正确 | 两个 Merchant 相同 reference |
| QUERY-E-001 | 订单不存在 | HTTP 404 | 不存在 reference |
| QUERY-S-001 | 查询响应脱敏 | 无 CVV<br>无 Expiry<br>卡号脱敏 | 特殊卡号 |

## 7. 流程测试

| 编号 | 测试场景 | 关键验证点 | 测试数据特殊点 |
|---|---|---|---|
| FLOW-N-001 | 支付完整流程 | 创建订单成功<br>数据库正确<br>收到 PAID 回调<br>查询 PROCESSING<br>收到 DELIVERED 回调<br>查询 DELIVERED | 保存 orderId<br>保存 reference<br>等待 30 秒 |
| FLOW-E-001 | 重复订单流程 | 第二次创建 409<br>数据库只有一条记录 | 同 merchant<br>同 reference |
| FLOW-S-001 | 跨商户访问 | 查询失败<br>Callback 不可访问 | MerchantA 创建<br>MerchantB 查询 |
