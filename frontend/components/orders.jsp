<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<jsp:include page="header.jsp" />

<div id="modal" class="modal-overlay">
    <div class="modal">
        <h3>⚠️ Cancel Order?</h3>
        <p>Are you sure you want to cancel this order? This action cannot be undone.</p>
        <div class="modal-btns">
            <button class="btn-modal-back" onclick="closeModal()">Keep Order</button>
            <button class="btn-modal-cancel" id="confirmCancelBtn" onclick="confirmCancel()">Yes, Cancel</button>
        </div>
    </div>
</div>

<div class="page-container" style="max-width: 1000px; margin: 40px auto; padding: 0 40px 60px; flex: 1; width: 100%;">
    <div class="page-header">
        <h1>📋 My Orders</h1>
        <p id="ordersSubtitle">Loading your orders...</p>
    </div>
    <div id="ordersList"></div>
</div>

<style>
    .page-header h1 { font-size: 32px; font-weight: 800; color: var(--text-main); letter-spacing: -1px; margin-bottom: 6px; }
    .page-header p { color: var(--text-muted); font-size: 15px; margin-bottom: 30px; font-weight: 500; }

    .order-card {
        background: rgba(255, 255, 255, 0.8); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px);
        border: 1px solid rgba(255, 255, 255, 0.5); border-radius: 24px; box-shadow: 0 8px 32px rgba(0,0,0,0.03);
        margin-bottom: 24px; overflow: hidden; animation: fadeIn 0.5s ease both; transition: all 0.3s ease;
    }
    .order-card:hover { transform: translateY(-4px); box-shadow: 0 12px 40px rgba(0,0,0,0.06); background: white; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }

    .order-header {
        display: flex; justify-content: space-between; align-items: center;
        padding: 24px 32px; background: rgba(0,0,0,0.02); border-bottom: 1px solid rgba(0,0,0,0.05);
        flex-wrap: wrap; gap: 10px;
    }
    .order-id { font-weight: 800; font-size: 16px; color: var(--text-main); display: block; margin-bottom: 4px; }
    .order-date { color: var(--text-muted); font-size: 14px; font-weight: 500; }

    .status-badge {
        padding: 8px 16px; border-radius: 50px; font-size: 12px; font-weight: 700;
        text-transform: uppercase; letter-spacing: 0.5px; box-shadow: 0 4px 12px rgba(0,0,0,0.05);
    }
    .status-PENDING { background: #FFFBEB; color: #D97706; border: 1px solid #FDE68A; }
    .status-PROCESSING { background: #EFF6FF; color: #2563EB; border: 1px solid #BFDBFE; }
    .status-SHIPPED { background: #EEF2FF; color: #4F46E5; border: 1px solid #C7D2FE; }
    .status-DELIVERED { background: #ECFDF5; color: #059669; border: 1px solid #A7F3D0; }
    .status-CANCELLED { background: #FEF2F2; color: #DC2626; border: 1px solid #FECACA; }

    .order-body { padding: 24px 32px; }
    .order-items { width: 100%; border-collapse: collapse; }
    .order-items th { text-align: left; color: var(--text-muted); font-size: 12px; font-weight: 600; padding: 12px 0; text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 2px solid rgba(0,0,0,0.05); }
    .order-items td { padding: 16px 0; font-size: 15px; color: var(--text-main); border-bottom: 1px solid rgba(0,0,0,0.05); font-weight: 500; }

    .order-footer {
        display: flex; justify-content: space-between; align-items: center;
        padding: 24px 32px; background: rgba(0,0,0,0.01);
    }
    .order-total { font-size: 18px; font-weight: 700; color: var(--text-muted); }
    .order-total span { color: var(--primary); font-size: 24px; font-weight: 800; margin-left: 8px; }

    .btn-cancel {
        padding: 10px 24px; border: none; border-radius: 12px;
        background: #FEF2F2; color: #DC2626; font-size: 14px; font-weight: 700; cursor: pointer;
        transition: all 0.3s ease;
    }
    .btn-cancel:hover { background: #FEE2E2; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(220, 38, 38, 0.2); }

    .empty-state { text-align: center; padding: 80px 20px; background: rgba(255, 255, 255, 0.7); backdrop-filter: blur(20px); border-radius: 32px; border: 1px solid rgba(255, 255, 255, 0.5); box-shadow: 0 12px 40px rgba(0,0,0,0.03); }
    .empty-state .icon { font-size: 80px; margin-bottom: 20px; }
    .empty-state h2 { font-size: 28px; font-weight: 800; margin-bottom: 12px; color: var(--text-main); }
    .empty-state p { color: var(--text-muted); margin-bottom: 32px; font-size: 16px; }
    .btn-shop { display: inline-block; padding: 16px 36px; background: var(--primary); color: white; border-radius: 50px; text-decoration: none; font-weight: 700; transition: all 0.3s; box-shadow: 0 8px 24px rgba(79, 70, 229, 0.25); }
    .btn-shop:hover { transform: translateY(-3px); box-shadow: 0 12px 32px rgba(79, 70, 229, 0.35); }

    /* Confirmation Modal */
    .modal-overlay { display: none; position: fixed; inset: 0; background: rgba(15, 23, 42, 0.4); backdrop-filter: blur(8px); -webkit-backdrop-filter: blur(8px); z-index: 500; justify-content: center; align-items: center; }
    .modal-overlay.show { display: flex; }
    .modal { background: rgba(255, 255, 255, 0.95); border-radius: 32px; box-shadow: 0 24px 64px rgba(0,0,0,0.15); padding: 40px; max-width: 450px; width: 90%; text-align: center; animation: fadeIn 0.3s ease; }
    .modal h3 { font-size: 24px; font-weight: 800; margin-bottom: 16px; color: var(--text-main); }
    .modal p { color: var(--text-muted); font-size: 16px; margin-bottom: 32px; line-height: 1.6; }
    .modal-btns { display: flex; gap: 16px; justify-content: center; }
    .modal-btns button { flex: 1; padding: 14px 24px; border-radius: 16px; font-size: 15px; font-weight: 700; cursor: pointer; border: none; transition: all 0.3s; }
    .btn-modal-cancel { background: #DC2626; color: white; box-shadow: 0 4px 12px rgba(220, 38, 38, 0.2); }
    .btn-modal-cancel:hover { background: #B91C1C; transform: translateY(-2px); box-shadow: 0 8px 20px rgba(220, 38, 38, 0.3); }
    .btn-modal-back { background: #F1F5F9; color: var(--text-main); }
    .btn-modal-back:hover { background: #E2E8F0; }

    @media (max-width: 600px) { .order-header { flex-direction: column; align-items: flex-start; } }
</style>

<script>
    let cancelOrderId = null;

    async function loadOrders() {
        const response = await fetch('/api/orders');
        const orders = await response.json();
        const container = document.getElementById('ordersList');
        const subtitle = document.getElementById('ordersSubtitle');

        if (orders.length === 0) {
            subtitle.textContent = '';
            container.innerHTML = '<div class="empty-state"><div class="icon">📦</div><h2>No orders yet</h2><p>Your order history will appear here</p><a href="/toys" class="btn-shop">Start Shopping</a></div>';
            return;
        }

        subtitle.textContent = orders.length + ' order' + (orders.length > 1 ? 's' : '');
        let html = '';
        orders.forEach((order, i) => {
            html += '<div class="order-card" style="animation-delay:' + (i*0.1) + 's">' +
                '<div class="order-header">' +
                    '<div><span class="order-id">Order #' + order.orderId + '</span><span class="order-date">Placed on ' + (order.orderDate || 'Unknown') + '</span></div>' +
                    '<span class="status-badge status-' + order.status + '">' + order.status + '</span>' +
                '</div>' +
                '<div class="order-body"><table class="order-items"><tr><th>Item</th><th>Qty</th><th>Price</th><th>Subtotal</th></tr>';

            if (order.items && order.items.length > 0) {
                order.items.forEach(item => {
                    html += '<tr><td>' + item.toyName + '</td><td>' + item.quantity + '</td><td>$' + item.price.toFixed(2) + '</td><td>$' + item.subtotal.toFixed(2) + '</td></tr>';
                });
            } else {
                html += '<tr><td colspan="4" style="color:var(--text-muted);text-align:center">Order details unavailable</td></tr>';
            }

            html += '</table></div><div class="order-footer"><div class="order-total">Total: <span>$' + order.totalAmount.toFixed(2) + '</span></div>';
            if (order.status === 'PENDING') {
                html += '<button class="btn-cancel" onclick="showCancelModal(' + order.orderId + ')">Cancel Order</button>';
            }
            html += '</div></div>';
        });
        container.innerHTML = html;
    }

    function showCancelModal(orderId) { cancelOrderId = orderId; document.getElementById('modal').classList.add('show'); }
    function closeModal() { document.getElementById('modal').classList.remove('show'); cancelOrderId = null; }

    async function confirmCancel() {
        if (!cancelOrderId) return;
        closeModal();
        const response = await fetch('/api/orders/cancel/' + cancelOrderId, { method: 'POST' });
        const data = await response.json();
        if(typeof showToast === 'function') showToast(data.message, !data.success);
        if (data.success) loadOrders();
    }

    document.getElementById('nav-orders').classList.add('active');
    loadOrders();
</script>

<jsp:include page="footer.jsp" />
