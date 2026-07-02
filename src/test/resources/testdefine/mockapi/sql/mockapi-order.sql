-- @name findOrderByReference
SELECT
    order_id,
    reference,
    merchant_id,
    amount,
    currency,
    payment_status,
    order_status,
    card_number_masked,
    fail_reason
FROM orders
WHERE merchant_id = :request.merchantId
  AND reference = :request.reference;
