<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<jsp:include page="header.jsp" />

<div class="page-container">
    <div class="page-header">
        <h1>🛒 Shopping Cart</h1>
        <p id="cartSubtitle">Loading your cart...</p>
    </div>
    <div id="cartContent" class="cart-layout"></div>
</div>

<style>
    /* Light Glassmorphism Cart Specific CSS */
    .page-container { max-width: 1100px; margin: 40px auto; padding: 0 40px; width: 100%; flex: 1; position: relative; z-index: 10; }
    .page-header { margin-bottom: 30px; animation: fadeUp 0.6s ease; }
    .page-header h1 { font-size: 32px; font-weight: 800; color: var(--text-main); letter-spacing: -1px; }
    .page-header p { color: var(--text-muted); font-size: 15px; margin-top: 6px; font-weight: 500; }

    .cart-layout { display: grid; grid-template-columns: 1fr 340px; gap: 30px; align-items: start; }

    /* Cart Items */
    .cart-items { display: flex; flex-direction: column; gap: 16px; }
    .cart-item {
        background: rgba(255, 255, 255, 0.8); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px);
        border: 1px solid rgba(255, 255, 255, 0.5); border-radius: 24px; box-shadow: 0 8px 32px rgba(0,0,0,0.03);
        padding: 20px; display: flex; align-items: center; gap: 20px;
        transition: all 0.3s ease; animation: fadeUp 0.5s ease-out both;
    }
    .cart-item:hover { transform: translateY(-4px); box-shadow: 0 12px 32px rgba(0,0,0,0.06); background: white; }
    @keyframes fadeUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

    .item-emoji { font-size: 40px; width: 70px; height: 70px; display: flex; align-items: center; justify-content: center; background: #F8FAFC; border-radius: 16px; }
    .item-image { width: 70px; height: 70px; border-radius: 16px; object-fit: cover; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
    .item-details { flex: 1; }
    .item-details h3 { font-size: 16px; font-weight: 700; margin-bottom: 4px; color: var(--text-main); }
    .item-details .item-price { color: var(--text-muted); font-size: 14px; font-weight: 600; }

    .quantity-control { display: flex; align-items: center; gap: 0; background: #F1F5F9; border-radius: 12px; padding: 4px; }
    .qty-btn { width: 32px; height: 32px; border: none; background: white; border-radius: 8px; color: var(--text-main); font-size: 16px; font-weight: 600; cursor: pointer; transition: all 0.2s; display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 6px rgba(0,0,0,0.05); }
    .qty-btn:hover { background: var(--primary); color: white; }
    .qty-value { width: 40px; text-align: center; color: var(--text-main); font-size: 14px; font-weight: 700; }

    .item-subtotal { text-align: right; min-width: 80px; }
    .item-subtotal .amount { font-size: 18px; font-weight: 800; color: var(--text-main); }
    .item-subtotal .label { font-size: 11px; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; font-weight: 600; }

    .btn-remove { background: none; border: none; color: #EF4444; cursor: pointer; font-size: 20px; padding: 8px; border-radius: 12px; transition: all 0.3s; opacity: 0.5; }
    .btn-remove:hover { opacity: 1; background: rgba(239, 68, 68, 0.1); transform: scale(1.1); }

    /* Order Summary */
    .order-summary {
        background: rgba(255, 255, 255, 0.9); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px);
        border: 1px solid rgba(255, 255, 255, 0.5); border-radius: 24px; box-shadow: 0 12px 40px rgba(0,0,0,0.05);
        padding: 32px; position: sticky; top: 120px; animation: fadeUp 0.8s ease;
    }
    .order-summary h3 { font-size: 20px; font-weight: 800; margin-bottom: 24px; color: var(--text-main); }
    .summary-row { display: flex; justify-content: space-between; margin-bottom: 16px; font-size: 15px; font-weight: 500; }
    .summary-row .label { color: var(--text-muted); }
    .summary-row.total { font-size: 22px; font-weight: 800; margin-top: 24px; padding-top: 24px; border-top: 1px solid rgba(0,0,0,0.05); }
    .summary-row.total .amount { color: var(--primary); }

    .btn-checkout {
        width: 100%; padding: 18px; border: none; border-radius: 16px;
        background: var(--primary); color: white; font-size: 16px; font-weight: 700; cursor: pointer;
        transition: all 0.3s ease; margin-top: 24px; box-shadow: 0 8px 24px rgba(79, 70, 229, 0.25);
    }
    .btn-checkout:hover { background: var(--primary-hover); transform: translateY(-3px); box-shadow: 0 12px 32px rgba(79, 70, 229, 0.35); }
    .btn-continue { display: block; text-align: center; color: var(--text-muted); text-decoration: none; font-size: 14px; font-weight: 600; margin-top: 20px; transition: color 0.3s; }
    .btn-continue:hover { color: var(--primary); }

    .empty-cart { text-align: center; padding: 80px 20px; grid-column: 1/-1; background: rgba(255, 255, 255, 0.7); backdrop-filter: blur(20px); border-radius: 32px; border: 1px solid rgba(255, 255, 255, 0.5); box-shadow: 0 12px 40px rgba(0,0,0,0.03); }
    .empty-cart .icon { font-size: 80px; margin-bottom: 20px; }
    .empty-cart h2 { font-size: 28px; font-weight: 800; margin-bottom: 12px; color: var(--text-main); }
    .empty-cart p { color: var(--text-muted); margin-bottom: 32px; font-size: 16px; }
    .btn-shop { display: inline-block; padding: 16px 36px; background: var(--primary); color: white; border-radius: 50px; text-decoration: none; font-weight: 700; transition: all 0.3s; box-shadow: 0 8px 24px rgba(79, 70, 229, 0.25); }
    .btn-shop:hover { transform: translateY(-3px); box-shadow: 0 12px 32px rgba(79, 70, 229, 0.35); }

    @media (max-width: 900px) { .cart-layout { grid-template-columns: 1fr; } .order-summary { position: static; } }
    @media (max-width: 600px) { .page-container { padding: 0 20px; } .cart-item { flex-wrap: wrap; } }
</style>

<script>
    async function loadCart() {
        const response = await fetch('/api/cart');
        const data = await response.json();
        if (!data.success) { window.location.href = '/login'; return; }

        const items = Object.values(data.items || {});
        const container = document.getElementById('cartContent');
        const subtitle = document.getElementById('cartSubtitle');

        if (items.length === 0) {
            subtitle.textContent = '';
            container.innerHTML = '<div class="empty-cart"><div class="icon">🛒</div><h2>Your cart is empty</h2><p>Browse our premium collection and add items to your cart</p><a href="/toys" class="btn-shop">Shop Collection</a></div>';
            return;
        }

        subtitle.textContent = items.length + ' item' + (items.length > 1 ? 's' : '') + ' in your cart';
        let total = 0;
        let itemsHtml = '<div class="cart-items">';
        
        // Fetch all toys to get images
        let toysMap = {};
        try {
            const toysRes = await fetch('/api/toys');
            const allToys = await toysRes.json();
            allToys.forEach(t => toysMap[t.id] = t);
        } catch(e) {}

        items.forEach((item, i) => {
            const subtotal = item.price * item.quantity;
            total += subtotal;
            
            const toyData = toysMap[item.toyId];
            let imgHtml = '<div class="item-emoji">🎁</div>';
            if (toyData && toyData.imageUrl && toyData.imageUrl.trim() !== '') {
                imgHtml = '<img class="item-image" src="' + toyData.imageUrl + '">';
            }

            itemsHtml += '<div class="cart-item" style="animation-delay:' + (i*0.05) + 's">' +
                imgHtml +
                '<div class="item-details"><h3>' + item.toyName + '</h3><span class="item-price">$' + item.price.toFixed(2) + ' each</span></div>' +
                '<div class="quantity-control">' +
                    '<button class="qty-btn" onclick="updateQty(' + item.toyId + ',' + (item.quantity-1) + ')">−</button>' +
                    '<div class="qty-value">' + item.quantity + '</div>' +
                    '<button class="qty-btn" onclick="updateQty(' + item.toyId + ',' + (item.quantity+1) + ')">+</button>' +
                '</div>' +
                '<div class="item-subtotal"><div class="label">Subtotal</div><div class="amount">$' + subtotal.toFixed(2) + '</div></div>' +
                '<button class="btn-remove" onclick="removeItem(' + item.toyId + ')" title="Remove">✕</button>' +
            '</div>';
        });
        itemsHtml += '</div>';

        const summaryHtml = '<div class="order-summary">' +
            '<h3>Order Summary</h3>' +
            '<div class="summary-row"><span class="label">Subtotal (' + items.length + ' items)</span><span>$' + total.toFixed(2) + '</span></div>' +
            '<div class="summary-row"><span class="label">Shipping</span><span style="color:var(--secondary)">Free</span></div>' +
            '<div class="summary-row total"><span>Total</span><span class="amount">$' + total.toFixed(2) + '</span></div>' +
            '<a href="/checkout" style="text-decoration:none;"><button class="btn-checkout">Proceed to Checkout</button></a>' +
            '<a href="/toys" class="btn-continue">Continue Shopping</a>' +
        '</div>';

        container.innerHTML = itemsHtml + summaryHtml;
    }

    async function updateQty(toyId, qty) {
        if (qty < 1) { removeItem(toyId); return; }
        const params = new URLSearchParams({ toyId, quantity: qty });
        await fetch('/api/cart/update', { method: 'PUT', body: params });
        loadCart();
        if(typeof updateCartCount === 'function') updateCartCount();
    }

    async function removeItem(toyId) {
        await fetch('/api/cart/remove/' + toyId, { method: 'DELETE' });
        showToast('Item removed from cart');
        loadCart();
        if(typeof updateCartCount === 'function') updateCartCount();
    }

    document.getElementById('nav-cart').classList.add('active');
    loadCart();
</script>

<jsp:include page="footer.jsp" />
