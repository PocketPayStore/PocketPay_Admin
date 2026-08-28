(() => {
    const PAGE_SIZE = 20;
    const state = {
        payments: [], cursorHistory: [0], pageIndex: 0, hasNext: false,
        selectedPaymentId: null, eventSource: null, connectedOnce: false,
        reconnecting: false, refreshTimer: null
    };
    const elements = {
        tableBody: document.querySelector('#attentionTableBody'), loading: document.querySelector('#loadingState'),
        empty: document.querySelector('#emptyState'), error: document.querySelector('#errorBanner'),
        errorMessage: document.querySelector('#errorMessage'), retry: document.querySelector('#retryButton'),
        refresh: document.querySelector('#refreshButton'), previous: document.querySelector('#previousButton'),
        next: document.querySelector('#nextButton'), page: document.querySelector('#pageNumber'),
        description: document.querySelector('#resultDescription'), connection: document.querySelector('#connectionState'),
        total: document.querySelector('#attentionTotalCount'), failed: document.querySelector('#failedCount'),
        timeout: document.querySelector('#timeoutCount'), stale: document.querySelector('#staleCount'),
        drawer: document.querySelector('#detailDrawer'), backdrop: document.querySelector('#drawerBackdrop'),
        close: document.querySelector('#closeDrawerButton'), detailLoading: document.querySelector('#detailLoading'),
        detailError: document.querySelector('#detailError'), detailContent: document.querySelector('#detailContent'),
        detailOrderNumber: document.querySelector('#detailOrderNumber'), detailStatus: document.querySelector('#detailStatus'),
        paymentDetails: document.querySelector('#paymentDetails'), timeline: document.querySelector('#statusTimeline')
    };
    const statusMeta = {
        READY: ['결제 대기', 'status-ready'], IN_PROGRESS: ['결제 처리 중', 'status-in-progress'],
        DONE: ['결제 완료', 'status-done'], FAILED: ['결제 실패', 'status-failed'],
        CANCELED: ['전체 취소', 'status-canceled'], PARTIAL_CANCELED: ['부분 취소', 'status-partial-canceled'],
        TIMEOUT_UNKNOWN: ['확인 필요', 'status-timeout-unknown']
    };

    const escapeHtml = value => String(value ?? '-').replaceAll('&', '&amp;').replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;').replaceAll('"', '&quot;').replaceAll("'", '&#039;');
    const formatAmount = amount => amount == null ? '-' : `${Number(amount).toLocaleString('ko-KR')}원`;
    const formatDate = value => value ? new Date(value).toLocaleString('ko-KR') : '-';
    const badge = status => {
        const [label, className] = statusMeta[status] || [status || '-', 'status-ready'];
        return `<span class="status-badge ${className}">${escapeHtml(label)}</span>`;
    };
    const elapsed = value => {
        const minutes = Math.max(0, Math.floor((Date.now() - new Date(value).getTime()) / 60000));
        if (minutes < 1) return '방금 전';
        if (minutes < 60) return `${minutes}분 전`;
        if (minutes < 1440) return `${Math.floor(minutes / 60)}시간 전`;
        return `${Math.floor(minutes / 1440)}일 전`;
    };

    async function loadAll(showLoading = true) {
        await Promise.all([loadPayments(showLoading), loadStatistics()]);
    }

    async function loadPayments(showLoading = true) {
        if (showLoading) elements.loading.hidden = false;
        elements.error.hidden = true;
        elements.refresh.classList.add('loading');
        try {
            const lastId = state.cursorHistory[state.pageIndex];
            const response = await fetch(`/api/payments/attention?lastId=${lastId}&size=${PAGE_SIZE + 1}`);
            if (!response.ok) throw new Error(await readError(response));
            const result = await response.json();
            state.hasNext = result.length > PAGE_SIZE;
            state.payments = result.slice(0, PAGE_SIZE);
            renderPayments();
        } catch (error) {
            elements.errorMessage.textContent = error.message;
            elements.error.hidden = false;
        } finally {
            elements.loading.hidden = true;
            elements.refresh.classList.remove('loading');
        }
    }

    async function loadStatistics() {
        try {
            const response = await fetch('/api/payments/attention/statistics');
            if (!response.ok) throw new Error();
            const data = await response.json();
            elements.total.textContent = data.totalCount.toLocaleString('ko-KR');
            elements.failed.textContent = data.failedCount.toLocaleString('ko-KR');
            elements.timeout.textContent = data.timeoutUnknownCount.toLocaleString('ko-KR');
            elements.stale.textContent = data.staleCount.toLocaleString('ko-KR');
        } catch {
            [elements.total, elements.failed, elements.timeout, elements.stale].forEach(item => item.textContent = '-');
        }
    }

    function renderPayments() {
        elements.tableBody.innerHTML = state.payments.map(payment => `
            <tr data-payment-id="${payment.id}">
                <td class="payment-id">#${payment.id}</td><td class="order-number">${escapeHtml(payment.orderNumber)}</td>
                <td class="amount">${formatAmount(payment.amount)}</td><td>${badge(payment.status)}</td>
                <td class="failure-reason" title="${escapeHtml(payment.failureMessage)}">${escapeHtml(payment.failureMessage || payment.failureCode)}</td>
                <td class="elapsed-time" data-updated-at="${payment.updatedAt}">${elapsed(payment.updatedAt)}</td>
                <td><button class="detail-button" type="button" data-payment-id="${payment.id}">상세보기</button></td>
            </tr>`).join('');
        elements.empty.hidden = state.payments.length !== 0;
        elements.description.textContent = state.payments.length ? `${state.payments.length}건을 표시하고 있습니다.` : '확인할 결제가 없습니다.';
        elements.previous.disabled = state.pageIndex === 0;
        elements.next.disabled = !state.hasNext;
        elements.page.textContent = `${state.pageIndex + 1} 페이지`;
    }

    async function readError(response) {
        try { return (await response.json()).message || `요청 실패 (${response.status})`; }
        catch { return `요청 실패 (${response.status})`; }
    }

    async function openDetail(paymentId) {
        state.selectedPaymentId = Number(paymentId);
        elements.drawer.classList.add('open'); elements.drawer.setAttribute('aria-hidden', 'false');
        elements.backdrop.hidden = false; document.body.style.overflow = 'hidden';
        await loadDetail();
    }

    async function loadDetail() {
        if (!state.selectedPaymentId) return;
        elements.detailLoading.hidden = false; elements.detailError.hidden = true; elements.detailContent.hidden = true;
        try {
            const [detailResponse, historyResponse] = await Promise.all([
                fetch(`/api/payments/${state.selectedPaymentId}`),
                fetch(`/api/payments/${state.selectedPaymentId}/histories`)
            ]);
            if (!detailResponse.ok || !historyResponse.ok) throw new Error();
            renderDetail(await detailResponse.json(), await historyResponse.json());
            elements.detailContent.hidden = false;
        } catch { elements.detailError.hidden = false; }
        finally { elements.detailLoading.hidden = true; }
    }

    function renderDetail(payment, histories) {
        elements.detailOrderNumber.textContent = payment.orderNumber;
        const [label, className] = statusMeta[payment.status] || [payment.status, 'status-ready'];
        elements.detailStatus.className = `status-badge ${className}`; elements.detailStatus.textContent = label;
        const details = [
            ['결제 ID', `#${payment.id}`], ['주문 ID', `#${payment.orderId}`], ['결제 금액', formatAmount(payment.amount)],
            ['결제 수단', payment.paymentMethod], ['PG사', payment.pgProvider], ['PG 거래 ID', payment.pgTransactionId],
            ['실패 코드', payment.failureCode], ['실패 사유', payment.failureMessage], ['생성 일시', formatDate(payment.createdAt)],
            ['최종 변경', formatDate(payment.updatedAt)]
        ];
        elements.paymentDetails.innerHTML = details.map(([key, value]) => `<div><dt>${key}</dt><dd>${escapeHtml(value)}</dd></div>`).join('');
        elements.timeline.innerHTML = histories.length
            ? histories.slice().reverse().map(history => `<li>${badge(history.status)}<small>${formatDate(history.createdAt)}</small></li>`).join('')
            : '<li><strong>상태 변경 이력이 없습니다.</strong></li>';
    }

    function closeDetail() {
        state.selectedPaymentId = null; elements.drawer.classList.remove('open');
        elements.drawer.setAttribute('aria-hidden', 'true'); elements.backdrop.hidden = true; document.body.style.overflow = '';
    }

    function connectSse() {
        state.eventSource = new EventSource('/api/payments/events');
        state.eventSource.addEventListener('connected', async () => {
            setConnection('connected', '실시간 연결');
            if (state.connectedOnce && state.reconnecting) await loadAll(false);
            state.connectedOnce = true; state.reconnecting = false;
        });
        state.eventSource.addEventListener('payment-status', event => {
            const data = JSON.parse(event.data);
            if (state.selectedPaymentId === data.paymentId) loadDetail();
            clearTimeout(state.refreshTimer);
            state.refreshTimer = setTimeout(() => loadAll(false), 400);
        });
        state.eventSource.addEventListener('heartbeat', () => setConnection('connected', '실시간 연결'));
        state.eventSource.onerror = () => { state.reconnecting = state.connectedOnce; setConnection('reconnecting', '재연결 중'); };
    }

    function setConnection(className, label) {
        elements.connection.className = `connection-state ${className}`;
        elements.connection.querySelector('.connection-label').textContent = label;
    }

    elements.refresh.addEventListener('click', () => loadAll(false));
    elements.retry.addEventListener('click', () => loadAll());
    elements.previous.addEventListener('click', () => { if (state.pageIndex > 0) { state.pageIndex--; loadPayments(); } });
    elements.next.addEventListener('click', () => {
        if (!state.hasNext || !state.payments.length) return;
        state.cursorHistory[state.pageIndex + 1] = state.payments.at(-1).id; state.pageIndex++; loadPayments();
    });
    elements.tableBody.addEventListener('click', event => {
        const button = event.target.closest('.detail-button'); if (button) openDetail(button.dataset.paymentId);
    });
    elements.close.addEventListener('click', closeDetail); elements.backdrop.addEventListener('click', closeDetail);
    document.addEventListener('keydown', event => { if (event.key === 'Escape') closeDetail(); });
    window.addEventListener('beforeunload', () => state.eventSource?.close());
    setInterval(() => document.querySelectorAll('[data-updated-at]').forEach(item => item.textContent = elapsed(item.dataset.updatedAt)), 60000);

    loadAll().finally(connectSse);
})();
