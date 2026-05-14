<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<jsp:include page="header.jsp" />

<div class="page-container" style="max-width: 700px; margin: 40px auto; padding: 0 40px 60px; animation: fadeIn 0.6s ease; flex: 1; width: 100%;">
    <!-- Progress Steps -->
    <div class="progress-steps">
        <div class="step completed"><div class="step-circle">✓</div><span class="step-label">Cart</span></div>
        <div class="step-line done"></div>
        <div class="step active"><div class="step-circle">2</div><span class="step-label">Checkout</span></div>
        <div class="step-line"></div>
        <div class="step pending"><div class="step-circle">3</div><span class="step-label">Confirm</span></div>
    </div>

    <!-- Shipping Info -->
    <div class="checkout-card">
        <h2><span class="icon">📦</span> Shipping Information</h2>
        <div class="form-group">
            <label>Full Name</label>
            <input type="text" id="fullName" value="${user.fullName}" readonly>
        </div>
        <div class="form-group">
            <label>Shipping Address</label>
            <textarea id="address" placeholder="Enter your full delivery address..."></textarea>
        </div>
    </div>

    <!-- Payment Method -->
    <div class="checkout-card">
        <h2><span class="icon">💳</span> Payment Method</h2>
        <div class="form-group">
            <label>Select Payment</label>
            <select id="payment">
                <option value="Credit Card">💳 Credit Card</option>
                <option value="Debit Card">🏧 Debit Card</option>
                <option value="Cash on Delivery">💵 Cash on Delivery</option>
            </select>
        </div>
    </div>

    <!-- Order Total -->
    <div class="total-display">
        <div class="label">Order Total</div>
        <div class="amount" id="total">$0.00</div>
    </div>

    <button class="btn-place-order" id="placeOrderBtn" onclick="placeOrder()">Place Order</button>
</div>

<style>
    /* Checkout Page CSS */
    @keyframes fadeIn { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

    /* Progress Steps */
    .progress-steps { display: flex; justify-content: center; align-items: center; gap: 0; margin-bottom: 40px; }
    .step { display: flex; align-items: center; gap: 12px; }
    .step-circle { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 14px; font-weight: 700; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
    .step.completed .step-circle { background: var(--secondary); color: white; box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3); }
    .step.active .step-circle { background: var(--primary); color: white; box-shadow: 0 4px 15px rgba(79, 70, 229, 0.3); }
    .step.pending .step-circle { background: white; color: var(--text-muted); border: 2px solid rgba(0,0,0,0.05); }
    .step-label { font-size: 14px; font-weight: 600; }
    .step.completed .step-label { color: var(--secondary); }
    .step.active .step-label { color: var(--primary); }
    .step.pending .step-label { color: var(--text-muted); }
    .step-line { width: 50px; height: 3px; background: rgba(0,0,0,0.05); margin: 0 16px; border-radius: 4px; }
    .step-line.done { background: var(--secondary); }

    .checkout-card { background: rgba(255, 255, 255, 0.8); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px); border: 1px solid rgba(255, 255, 255, 0.5); border-radius: 24px; padding: 32px; margin-bottom: 24px; box-shadow: 0 8px 32px rgba(0,0,0,0.03); transition: all 0.3s; }
    .checkout-card:hover { transform: translateY(-2px); box-shadow: 0 12px 40px rgba(0,0,0,0.06); background: white; }
    .checkout-card h2 { font-size: 20px; font-weight: 700; margin-bottom: 24px; display: flex; align-items: center; gap: 12px; color: var(--text-main); }
    .checkout-card h2 .icon { font-size: 24px; }

    .form-group { margin-bottom: 24px; }
    .form-group label { display: block; font-size: 14px; font-weight: 600; color: var(--text-muted); margin-bottom: 8px; }
    .form-group input, .form-group select, .form-group textarea {
        width: 100%; padding: 16px 20px;
        background: #F8FAFC; border: 1px solid rgba(0,0,0,0.05);
        border-radius: 16px; font-size: 15px; color: var(--text-main);
        transition: all 0.3s ease; outline: none; font-weight: 500;
    }
    .form-group input:focus, .form-group textarea:focus, .form-group select:focus { border-color: var(--primary); background: white; box-shadow: 0 0 0 4px rgba(79, 70, 229, 0.1); }
    .form-group textarea { min-height: 100px; resize: vertical; }
    .form-group input[readonly] { opacity: 0.7; cursor: not-allowed; background: rgba(0,0,0,0.02); }

    .total-display { text-align: center; padding: 32px; background: rgba(79, 70, 229, 0.05); border: 1px solid rgba(79, 70, 229, 0.1); border-radius: 24px; margin-bottom: 32px; }
    .total-display .label { font-size: 15px; color: var(--text-muted); font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 8px; }
    .total-display .amount { font-size: 48px; font-weight: 800; color: var(--primary); letter-spacing: -1px; }

    .btn-place-order {
        width: 100%; padding: 20px; border: none; border-radius: 50px;
        background: var(--primary); color: white; font-size: 18px; font-weight: 700; cursor: pointer;
        transition: all 0.3s ease; box-shadow: 0 10px 30px rgba(79, 70, 229, 0.3);
    }
    .btn-place-order:hover { transform: translateY(-4px); box-shadow: 0 15px 40px rgba(79, 70, 229, 0.4); background: var(--primary-hover); }
    .btn-place-order.loading { pointer-events: none; opacity: 0.7; transform: none; box-shadow: none; }
</style>

<script>
    async function loadTotal() {
        const response = await fetch('/api/cart');
        const data = await response.json();
        if (data.success) {
            document.getElementById('total').textContent = '$' + data.total.toFixed(2);
        }
    }

    async function placeOrder() {
        const address = document.getElementById('address').value.trim();
        const payment = document.getElementById('payment').value;
        const btn = document.getElementById('placeOrderBtn');

        if (!address) { if(typeof showToast === 'function') showToast('Please enter your shipping address', true); return; }

        btn.classList.add('loading');
        btn.textContent = 'Processing order...';

        try {
            const params = new URLSearchParams({ shippingAddress: address, paymentMethod: payment });
            const response = await fetch('/api/orders/place', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params
            });
            const data = await response.json();

            if (data.success) {
                if(typeof showToast === 'function') showToast('Order placed successfully!');
                setTimeout(() => window.location.href = '/orders', 1500);
            } else {
                if(typeof showToast === 'function') showToast(data.message, true);
                btn.classList.remove('loading');
                btn.textContent = 'Place Order';
            }
        } catch(e) {
            if(typeof showToast === 'function') showToast('Error placing order. Try again.', true);
            btn.classList.remove('loading');
            btn.textContent = 'Place Order';
        }
    }

    loadTotal();
</script>

<jsp:include page="footer.jsp" />
