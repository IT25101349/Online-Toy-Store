<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<jsp:include page="admin-header.jsp" />

<h1 class="page-title">📦 Order Management</h1>
<p class="page-subtitle">View and update order statuses</p>

<div class="section-card">
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <c:if test="${empty orders}">
        <div class="empty-msg">📭 No orders yet</div>
    </c:if>

    <c:if test="${not empty orders}">
        <table>
            <thead><tr><th>Order ID</th><th>Customer</th><th>Date</th><th>Total</th><th>Current Status</th><th>Change Status</th></tr></thead>
            <tbody>
                <c:forEach items="${orders}" var="order">
                    <tr>
                        <td style="font-weight:700; color: var(--text-dark);">#${order.orderId}</td>
                        <td style="font-weight: 500;">${order.userName}</td>
                        <td style="color: var(--gray);">${order.orderDate}</td>
                        <td style="font-weight:800;color:var(--success)">$${order.totalAmount}</td>
                        <td><span class="status-badge badge-${order.status}">${order.status}</span></td>
                        <td>
                            <select class="status-select" data-order-id="${order.orderId}" onchange="updateStatus(this)">
                                <option value="PENDING" ${order.status == 'PENDING' ? 'selected' : ''}>PENDING</option>
                                <option value="PROCESSING" ${order.status == 'PROCESSING' ? 'selected' : ''}>PROCESSING</option>
                                <option value="SHIPPED" ${order.status == 'SHIPPED' ? 'selected' : ''}>SHIPPED</option>
                                <option value="DELIVERED" ${order.status == 'DELIVERED' ? 'selected' : ''}>DELIVERED</option>
                                <option value="CANCELLED" ${order.status == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                            </select>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>

<style>
    .status-badge { padding: 6px 14px; border-radius: 50px; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }
    .badge-PENDING { background: #FFFBEB; color: #D97706; border: 1px solid #FDE68A; }
    .badge-PROCESSING { background: #EFF6FF; color: #2563EB; border: 1px solid #BFDBFE; }
    .badge-SHIPPED { background: #EEF2FF; color: #4F46E5; border: 1px solid #C7D2FE; }
    .badge-DELIVERED { background: #ECFDF5; color: #059669; border: 1px solid #A7F3D0; }
    .badge-CANCELLED { background: #FEF2F2; color: #DC2626; border: 1px solid #FECACA; }

    .status-select {
        padding: 10px 16px; border: 1px solid rgba(0,0,0,0.05); border-radius: 12px;
        font-size: 13px; font-family: inherit; font-weight: 600; cursor: pointer;
        background: #F8FAFC; color: var(--text-dark); outline: none; transition: all 0.3s;
    }
    .status-select:focus { border-color: var(--primary); background: white; box-shadow: 0 0 0 4px rgba(79, 70, 229, 0.1); }

    .empty-msg { text-align: center; padding: 60px 20px; color: var(--gray); font-size: 18px; font-weight: 600; }
</style>

<script>
    document.getElementById('nav-orders').classList.add('active');

    async function updateStatus(selectElement) {
        const orderId = selectElement.getAttribute('data-order-id');
        const newStatus = selectElement.value;

        if (!confirm('Change order #' + orderId + ' to ' + newStatus + '?')) {
            location.reload();
            return;
        }

        try {
            const params = new URLSearchParams();
            params.append('status', newStatus);
            const response = await fetch('/admin/api/orders/' + orderId + '/status', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params.toString()
            });

            if (!response.ok) throw new Error('HTTP error: ' + response.status);
            const data = await response.json();

            if (data.success) {
                showToast('Status updated to ' + newStatus, 'success');
                setTimeout(() => location.reload(), 1200);
            } else {
                showToast('Error: ' + data.message, 'error');
                location.reload();
            }
        } catch (error) {
            showToast('Failed to update status', 'error');
            location.reload();
        }
    }
</script>

<jsp:include page="admin-footer.jsp" />
