package com.vayo.fitcheq.data.model

data class BrandInfo(
    val name: String,
    val shippingPolicy: String,
    val returnPolicy: String,
    val exchangePolicy: String = "",
    val instagramUrl: String = "",
    val websiteUrl: String = "",
    val supportEmail: String = "",
    val logoUrl: String = "",
)

val brandMap = mapOf(
    "untld" to BrandInfo(
        name = "UNTLD",
        shippingPolicy = """
            • Ships in 24–36 hrs (if ordered before 1 PM).
            • Delivery in 4–5 days.
            • Remote areas (J&K, Northeast): 8–10 days.
        """.trimIndent(),
        returnPolicy = """
            • Exchange/return within 3 days of delivery.
            • No returns on international orders.
            • No returns on already exchanged items.
        """.trimIndent(),
        exchangePolicy = """
            • One-time exchange allowed.
            • No exchange on sale or COD orders.
            • COD charges (₹150) and shipping are non-refundable.
        """.trimIndent(),
        instagramUrl = "https://www.instagram.com/untld.in/",
        websiteUrl = "https://untld.in",
        logoUrl = "https://untld.in/cdn/shop/files/Untitled_1000_x_1000_px.png?v=1661777427&width=500",
    ),
    "ajio" to BrandInfo(
        name = "AJIO",
        shippingPolicy = """
               • Delivery in 2–9 working days, based on your pin code.
               • You can check delivery estimates on the product page using your pin code.
            """.trimIndent(),
        returnPolicy = """
               • Return within 15 days (or 10 days during Winter Sale).
               • Product must be unused, unwashed, and returned with tags, original packaging, and any gifts.
            """.trimIndent(),
        instagramUrl = "https://www.instagram.com/ajiolife/",
        websiteUrl = "https://www.ajio.com/",
        logoUrl = "https://assets.ajio.com/static/img/Ajio-Logo.svg",
    ),
    "urbano" to BrandInfo(
        name = "URBANO",
        shippingPolicy = """
            • Dispatched in 1–2 days
            • Delivery in 2–5 days
            • Metros: 2–3 days
            • Rest of India: 3–5 days
            • Free shipping on all orders
            """.trimIndent(),
        returnPolicy = """
            • 15-day return/exchange window
            • Items must be unused and with original tags
            • Refund via Urbano Credits or original payment method
            """.trimIndent(),
        instagramUrl = "https://www.instagram.com/urbano_fashion_",
        websiteUrl = "https://www.urbanofashion.com/",
        logoUrl = "https://www.urbanofashion.com/cdn/shop/files/Logo_Urbano_50ht.svg?v=1684135107",
    ),
    "one fit" to BrandInfo(
        name = "ONE FIT",
        shippingPolicy = """
            • We currently offer free shipping worldwide on all orders over 1099/-
            """.trimIndent(),
        returnPolicy = """
            • Return or exchange within 4 days of delivery
            • INR 99/- exchange fee (goes to courier company)
            • For COD orders, refund is given as wallet credit
            • Prepaid orders get bank refund (after quality check)
            • Standard shipping fee (₹110) is non-refundable
            • Products must be unused and in original condition
            • Pickup attempted twice — otherwise self-ship required
            • Pre-order/custom products are non-returnable
            • Partial returns get store credit only
            """.trimIndent(),
        instagramUrl = "https://www.instagram.com/1fit.in",
        websiteUrl = "https://1fit.in/",
        logoUrl = "https://1fit.in/wp-content/uploads/2024/09/cropped-cropped-Image-Placeholder.png",
    ),
    "trapin" to BrandInfo(
        name = "TRAPIN",
        shippingPolicy = """
            • Orders processed within 24 hrs, shipped in 2–3 days
            • Metro delivery: 2–3 days | Tier 1 cities: 4–6 days
            • Free shipping on all prepaid orders
            • COD charges shown at checkout
            • Delivery time may vary due to courier partners
            """.trimIndent(),
        returnPolicy = """
            • Return within 7 days if unsatisfied
            • No exchange — place new order after return
            • One-time return allowed per order
            • Return pickup attempted twice, else self-ship
            • ₹150 pickup fee deducted from credit note
            • Sale items are non-returnable & non-refundable
            • Returned items must be unused with tags
            • Credit note valid for 3 months
            • Late returns (after 7 days): ₹300 deduction
            """.trimIndent(),
        instagramUrl = "https://www.instagram.com/trapinindia/",
        websiteUrl = "https://trapin.co/",
        logoUrl = "https://trapin.co/cdn/shop/files/Trapin_White_ac0e4112-6ce7-4b96-a459-5d1f1e6f08e6.png?v=1726561310&width=165",
    ),
    "5 feet 11" to BrandInfo(
        name = "5 Feet 11",
        shippingPolicy = """
            • Dispatched in 1–2 business days (up to 7 for pre-order/made-to-order items)
            • Delivery within 5–7 days from dispatch
            • Free shipping on prepaid orders
            • COD orders: ₹99 shipping fee
            • Prices include GST
            """.trimIndent(),
        returnPolicy = """
        • Return/exchange within 72 hrs (excludes sale & made-to-order items)
        • ₹100 fee per product for return/exchange
        • Prepaid orders: refund to bank (7–10 business days)
        • COD orders: store credit within 7–10 days
        • Reverse pickup charge of ₹100 per item applies
        """.trimIndent(),
        instagramUrl = "https://www.instagram.com/5feet11/",
        websiteUrl = "https://www.5feet11.com/",
        logoUrl = "https://www.5feet11.com/cdn/shop/files/5feet11-light-png.png?crop=center&height=360&v=1739774879&width=744",
    ),
    "blurg village" to BrandInfo(
        name = "Blurg Village",
        shippingPolicy = """
            • Delivery within 10 days
            """.trimIndent(),
        returnPolicy = """
            • No refunds, exchanges, or cancellations
            • Damaged items eligible for replacement (unboxing video required from start to end)
            """.trimIndent(),
        instagramUrl = "https://www.instagram.com/blurgvillage",
        websiteUrl = "https://blurgvillage.com/",
        logoUrl = "https://blurgvillage.com/wp-content/uploads/2025/06/IMG_5610-NEW-800x800.png?crop=1",
    ),
    "banana club" to BrandInfo(
        name = "BANANA CLUB",
        shippingPolicy = """
• Shipped within 24–48 hours
• Delivery in 5–7 days (may arrive sooner)
• Tracking sent via email & WhatsApp after dispatch
""".trimIndent(),
        returnPolicy = """
• Return/exchange within 7 days
• Report damaged/incorrect items within 24–48 hrs
• Pickup details sent via SMS/email — don't hand over items without confirmation
• Refunds processed within 2–3 days after QC
• Prepaid: refunded to original method (3–5 days)
• COD: refunded as gift card to email/phone
• Discount code usage may affect refund value
""".trimIndent(),
        instagramUrl = "https://www.instagram.com/_banana_club",
        websiteUrl = "https://bananaclub.co.in/",
        logoUrl = "https://bananaclub.co.in/cdn/shop/files/banana-club_520x.png?v=1681805284",
    ),
    "botnia" to BrandInfo(
        name = "BOTNIA",
        shippingPolicy = """
• Free shipping on prepaid orders (COD fee extra)
• Dispatched within 1 day (except custom items)
• Delivery in 5–9 working days (may vary by pincode)
• Orders placed Sat after 5PM ship on Monday
""".trimIndent(),
        returnPolicy = """
• 7-day return/exchange window from delivery
• ₹99 reverse pickup fee per order (not per item)
• Items must be unused, unwashed & with original tags
• Sale/bundle/clearance items: no returns, only size exchange (₹99 fee)
• Store credit for refunds; COD orders eligible for exchange only
• Re-shipping costs ₹99 (or choose self-ship)
• Discount codes don’t make products final sale
• No return/exchange on CRED sale items
• Order cancellations accepted within 24 hrs via email
""".trimIndent(),
        instagramUrl = "https://www.instagram.com/botnia.in",
        websiteUrl = "https://www.botnia.in/",
        logoUrl = "https://www.botnia.in/cdn/shop/files/Botnia_Logo-01_W_800x.png?v=1731577710",
    ),
    "myntra" to BrandInfo(
        name = "Myntra",
        shippingPolicy = """
            • Order Tracking: Track orders in MyMyntra → Track Orders.
            • Platform Fee: Charged to maintain smooth platform operations.
                • Refunded if order is lost/undelivered or if full order is cancelled.
                • Not refunded for partial cancellations/returns or accounts flagged under Fair Usage Policy.
            • Fair Usage Policy: Accounts with excessive returns or non-acceptance of shipments may lose refund eligibility for platform fees.
            • PIN Code Serviceability: Check on product page via “Check Delivery Options.”
            """.trimIndent(),
        returnPolicy = """
            • Return/Exchange Window: As per product details page. Item must be unused, with original tags/packaging intact.
            • Options:
                • Pick-up: Free service in most locations (refund/exchange initiated after quality check).
                • Self-ship: Allowed if pickup not available. Courier charges reimbursed in Myntra credits with receipt proof.
            • Refunds: Doorstep refund or post-warehouse quality check.
            • Exchanges:
                • Allowed for size/style change or different items (value differences adjusted).
                • Delivered to the same address only.
                • Subject to stock availability & serviceability.
            • Exceptions (Non-returnable/Exchangeable): Innerwear, lingerie (with briefs), socks, swimwear, shapewear bottoms, mittens, wrist-bands, Swarovski/precious jewelry, Ray-Ban sunglasses, some fine jewelry, watches, sherwanis (exchange only).
            • Rules:
                • Product must pass quality check.
                • Free gifts/offers must also be returned if the main item is.
                • Self-shipped returns must be securely packed.
                • Refunds/exchanges may be restricted if policy is misused.
            """.trimIndent(),
        instagramUrl = "https://www.instagram.com/myntra/?hl=en",
        websiteUrl = "https://www.myntra.com/",
    ),
    "amazon" to BrandInfo(
        name = "Amazon",
        shippingPolicy = """
            • Options: Standard, Same-Day, One-Day (eligible items/locations).
            • Fees: Vary by item size, weight, and delivery option.
            • Prime Members: Free fast delivery on eligible items (e.g., 2-day).
            • Fulfilled by Amazon (FBA): Amazon handles storage, packing, delivery; usually Prime-eligible.
            • Other Sellers: Policies vary (Easy Ship, Self-Ship).
International Shipping: Limited; most items not shipped outside India.
""".trimIndent(),
        returnPolicy = """
• General Policy: Returns allowed within product-specific Returns Window.
• Return Windows: 2–10 days (varies by category).
• Replacement vs. Refund: Defective/damaged items often replaced; refund only if replacement unavailable.
• Non-Returnable Items: Hygiene, health, consumables, cosmetics, innerwear, certain food items.
• Condition: Must be unused, original packaging & accessories intact.
• Process: Initiate via Your Orders → Amazon arranges pickup.
• Seller-Specific Policies: May vary, check product page.
• Policy Abuse: Amazon may charge fees or restrict accounts for misuse/fraud.
""".trimIndent(),
        instagramUrl = "https://www.instagram.com/amazondotin/?hl=en",
        websiteUrl = "https://www.amazon.in/",
    ),
    "the bear house" to BrandInfo(
        name = "The Bear House",
        shippingPolicy = """
            • Processing Time: Orders shipped within 1 business day.
            • Charges: Free shipping on prepaid orders; ₹100 fee on COD orders.
            • Tracking: Details shared via WhatsApp, Email, or SMS.
""".trimIndent(),
        returnPolicy = """
• Initiation: Only via Bear House mobile app (iOS & Android).
• Window: 7 days from delivery; ₹100 reverse shipping fee.
• Exchange: Once per item, size availability only. No re-exchange.
• Sale Orders: No returns; one exchange allowed (same/higher value).
• Pickup: 2 attempts, else self-ship. Depends on pin code.
• Condition: Must be unused, with original tags/packaging.
• Refunds:
    • Prepaid: Original payment method.
    • COD: Store credits (valid 6 months).
• Exclusions: Accessories, socks, innerwear, flat store items.
""".trimIndent(),
        instagramUrl = "https://www.instagram.com/bearhouseindia/?hl=en",
        websiteUrl = "https://thebearhouse.com/collections/newarrival?utm_content=&utm_term=&gad_campaignid=22589529038&sort_by=created-descending",
    ),
    "newme" to BrandInfo(
        name = "NEWME",
        shippingPolicy = """
            • Fee: ₹49 on all orders (non-refundable).
            • Standard Delivery: 10–15 business days.
            • Expedited Options:
                • 90-min Delivery: ₹49.
                • 4-hr / Same-Day / Next-Day: Free above ₹999; ₹49 below ₹999; ₹99 below ₹499.
            • International: Standard delivery in 10–15 days (check product page for details).
""".trimIndent(),
        returnPolicy = """
• Window: 3 days from delivery.
• Dresses & Gowns: ₹300 reverse pickup/exchange fee.
• Condition: Must be unused, with original packaging, tags, labels, and invoice.
• Partial Returns: Allowed (must return complete item with gifts/freebies).
• Exchange: Only once per order; exchanged items cannot be refunded.
• Refunds:
    • Order Cancellation: Full refund incl. shipping.
    • Post-Delivery Return: Shipping & COD fees not refunded.
    • COD Orders: Refund credited to Newmi Wallet (lifetime validity).
• Non-Returnable Items: Some categories (e.g., healthcare devices, electronics, yoga mats); eligible for replacement within 7 days if defective/damaged.
• Return Handling Fee: ₹100 may apply for unusual/repeated return activity.
""".trimIndent(),
        instagramUrl = "https://www.instagram.com/newme.asia/?hl=en",
        websiteUrl = "https://newme.asia/"
    ),

)