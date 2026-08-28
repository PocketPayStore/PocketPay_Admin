(() => {
    const PAGE_SIZE = 20;
    const state = {
        payments: [],
        cursorHistory: [0],
        pageIndex: 0,
        hasNext: false,
        selectedPaymentId: null,
        eventSource: null,
        connectedOnce: false,
        reconnecting: false,
        requestController: null,
        refreshTimer: null,
        statisticsTimer: null
    };

    const elements = {
        searchForm: document.querySelector('#searchForm'),
        resetButton: document.querySelector('#resetButton'),
        retryButton: document.querySelector('#retryButton'),
        refreshButton: document.querySelector('#refreshButton'),
        previousButton: document.querySelector('#previousButton'),
        nextButton: document.querySelector('#nextButton'),
        pageNumber: document.querySelector('#pageNumber'),
        tableBody: document.querySelector('#paymentTableBody'),
        loadingState: document.querySelector('#loadingState'),
        emptyState: document.querySelector('#emptyState'),
        errorBanner: document.querySelector('#errorBanner'),
        errorMessage: document.querySelector('#errorMessage'),
        resultDescription: document.querySelector('#resultDescription'),
        connectionState: document.querySelector('#connectionState'),
        totalCount: document.querySelector('#totalCount'),
        todayCount: document.querySelector('#todayCount'),
        todayAmount: document.querySelector('#todayAmount'),
        todayDoneCount: document.querySelector('#todayDoneCount'),
        todayAttentionCount: document.querySelector('#todayAttentionCount'),
        drawer: document.querySelector('#detailDrawer'),
        drawerBackdrop: document.querySelector('#drawerBackdrop'),
        closeDrawerButton: document.querySelector('#closeDrawerButton'),
        detailLoading: document.querySelector('#detailLoading'),
        detailError: document.querySelector('#detailError'),
        detailContent: document.querySelector('#detailContent'),
        detailOrderNumber: document.querySelector('#detailOrderNumber'),
        detailStatus: document.querySelector('#detailStatus'),
        paymentDetails: document.querySelector('#paymentDetails'),
        statusTimeline: document.querySelector('#statusTimeline')
    };

    const statusMeta = {
        READY: ['결제 대기', 'status-ready'],
        IN_PROGRESS: ['결제 처리 중', 'status-in-progress'],
        DONE: ['결제 완료', 'status-done'],
        FAILED: ['결제 실패', 'status-failed'],
        CANCELED: ['전체 취소', 'status-canceled'],
        PARTIAL_CANCELED: ['부분 취소', 'status-partial-canceled'],
        TIMEOUT_UNKNOWN: ['확인 필요', 'status-timeout-unknown']
    };

    function statusBadge(status) {
        const [label, className] = statusMeta[status] || [status || '-', 'status-ready'];
        return `<span class="status-badge ${className}">${escapeHtml(label)}</span>`;
    }

    function formatAmount(amount) {
        return amount == null ? '-' : `${Number(amount).toLocaleString('ko-KR')}원`;
    }

    function formatDate(value) {
        if (!value) return '-';
        const date = new Date(value);
        return Number.isNaN(date.getTime()) ? value : date.toLocaleString('ko-KR', {
            year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit'
        });
    }

    function escapeHtml(value) {
        return String(value ?? '-')
            .replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;').replaceAll("'", '&#039;');
    }

    function buildSearchParams() {
        const formData = new FormData(elements.searchForm);
        const params = new URLSearchParams({
            lastId: String(state.cursorHistory[state.pageIndex]),
            size: String(PAGE_SIZE + 1)
        });
        for (const [key, value] of formData.entries()) {
            if (value) params.set(key, value);
        }
        return params;
    }

    async function loadPayments(showLoading = true) {
        state.requestController?.abort();
        state.requestController = new AbortController();
        if (showLoading) setListLoading(true);
        elements.errorBanner.hidden = true;
        elements.refreshButton.classList.add('loading');

        try {
            const response = await fetch(`/api/payments?${buildSearchParams()}`, {
                headers: { Accept: 'application/json' }, signal: state.requestController.signal
            });
            if (!response.ok) throw new Error(await readError(response));
            const result = await response.json();
            state.hasNext = result.length > PAGE_SIZE;
            state.payments = result.slice(0, PAGE_SIZE);
            renderPayments();
        } catch (error) {
            if (error.name !== 'AbortError') showListError(error.message);
        } finally {
            elements.refreshButton.classList.remove('loading');
            setListLoading(false);
        }
    }

    function renderPayments() {
        elements.tableBody.innerHTML = state.payments.map(payment => `
            <tr data-payment-id="${payment.id}">
                <td class="payment-id">#${payment.id}</td>
                <td class="order-number">${escapeHtml(payment.orderNumber)}</td>
                <td class="amount">${formatAmount(payment.amount)}</td>
                <td data-field="status">${statusBadge(payment.status)}</td>
                <td data-field="updatedAt">${formatDate(payment.updatedAt)}</td>
                <td><button class="detail-button" type="button" data-action="detail" data-payment-id="${payment.id}">상세보기</button></td>
            </tr>`).join('');

        elements.emptyState.hidden = state.payments.length !== 0;
        elements.resultDescription.textContent = state.payments.length
            ? `${state.payments.length}건의 결제를 표시하고 있습니다.`
            : '조건에 맞는 결제가 없습니다.';
        elements.previousButton.disabled = state.pageIndex === 0;
        elements.nextButton.disabled = !state.hasNext;
        elements.pageNumber.textContent = `${state.pageIndex + 1} 페이지`;
    }

    async function loadStatistics() {
        try {
            const response = await fetch('/api/payments/statistics', { headers: { Accept: 'application/json' } });
            if (!response.ok) throw new Error();
            const statistics = await response.json();
            elements.totalCount.textContent = statistics.totalCount.toLocaleString('ko-KR');
            elements.todayCount.textContent = statistics.todayCount.toLocaleString('ko-KR');
            elements.todayAmount.textContent = formatAmount(statistics.todayAmount);
            elements.todayDoneCount.textContent = statistics.todayDoneCount.toLocaleString('ko-KR');
            elements.todayAttentionCount.textContent = statistics.todayAttentionCount.toLocaleString('ko-KR');
        } catch {
            [elements.totalCount, elements.todayCount, elements.todayAmount,
                elements.todayDoneCount, elements.todayAttentionCount].forEach(element => element.textContent = '-');
        }
    }

    function setListLoading(loading) {
        elements.loadingState.hidden = !loading;
        if (loading) elements.emptyState.hidden = true;
    }

    function showListError(message) {
        elements.errorMessage.textContent = message || '잠시 후 다시 시도해주세요.';
        elements.errorBanner.hidden = false;
        elements.resultDescription.textContent = '조회 중 오류가 발생했습니다.';
    }

    async function readError(response) {
        try {
            const body = await response.json();
            return body.message || `요청 실패 (${response.status})`;
        } catch {
            return `요청 실패 (${response.status})`;
        }
    }

    async function openDetail(paymentId) {
        state.selectedPaymentId = Number(paymentId);
        elements.drawer.classList.add('open');
        elements.drawer.setAttribute('aria-hidden', 'false');
        elements.drawerBackdrop.hidden = false;
        document.body.style.overflow = 'hidden';
        await loadDetail();
    }

    async function loadDetail() {
        if (!state.selectedPaymentId) return;
        elements.detailLoading.hidden = false;
        elements.detailError.hidden = true;
        elements.detailContent.hidden = true;
        try {
            const [detailResponse, historyResponse] = await Promise.all([
                fetch(`/api/payments/${state.selectedPaymentId}`),
                fetch(`/api/payments/${state.selectedPaymentId}/histories`)
            ]);
            if (!detailResponse.ok) throw new Error(await readError(detailResponse));
            if (!historyResponse.ok) throw new Error(await readError(historyResponse));
            renderDetail(await detailResponse.json(), await historyResponse.json());
            elements.detailContent.hidden = false;
        } catch {
            elements.detailError.hidden = false;
        } finally {
            elements.detailLoading.hidden = true;
        }
    }

    function renderDetail(payment, histories) {
        elements.detailOrderNumber.textContent = payment.orderNumber;
        elements.detailStatus.className = `status-badge ${(statusMeta[payment.status] || [null, 'status-ready'])[1]}`;
        elements.detailStatus.textContent = (statusMeta[payment.status] || [payment.status])[0];
        const details = [
            ['결제 ID', `#${payment.id}`], ['주문 ID', `#${payment.orderId}`],
            ['결제 금액', formatAmount(payment.amount)], ['사용 포인트', formatAmount(payment.usedPointAmount)],
            ['환불 가능 금액', formatAmount(payment.refundableAmount)], ['결제 수단', payment.paymentMethod],
            ['PG사', payment.pgProvider], ['PG 거래 ID', payment.pgTransactionId],
            ['멱등키', payment.idempotencyKey], ['승인 일시', formatDate(payment.approvedAt)],
            ['생성 일시', formatDate(payment.createdAt)], ['최종 변경', formatDate(payment.updatedAt)],
            ['실패 코드', payment.failureCode], ['실패 사유', payment.failureMessage]
        ];
        elements.paymentDetails.innerHTML = details.map(([label, value]) =>
            `<div><dt>${escapeHtml(label)}</dt><dd>${escapeHtml(value)}</dd></div>`).join('');
        elements.statusTimeline.innerHTML = histories.length
            ? histories.slice().reverse().map(history => `<li>${statusBadge(history.status)}<small>${formatDate(history.createdAt)}</small></li>`).join('')
            : '<li><strong>상태 변경 이력이 없습니다.</strong></li>';
    }

    function closeDetail() {
        state.selectedPaymentId = null;
        elements.drawer.classList.remove('open');
        elements.drawer.setAttribute('aria-hidden', 'true');
        elements.drawerBackdrop.hidden = true;
        document.body.style.overflow = '';
    }

    function connectSse() {
        state.eventSource = new EventSource('/api/payments/events');
        setConnectionState('connecting', '연결 중');

        state.eventSource.addEventListener('connected', async () => {
            setConnectionState('connected', '실시간 연결');
            if (state.connectedOnce && state.reconnecting) {
                await Promise.all([loadPayments(false), loadStatistics()]);
                await loadDetail();
            }
            state.connectedOnce = true;
            state.reconnecting = false;
        });
        state.eventSource.addEventListener('payment-status', event => applyPaymentEvent(JSON.parse(event.data)));
        state.eventSource.addEventListener('heartbeat', () => setConnectionState('connected', '실시간 연결'));
        state.eventSource.onerror = () => {
            state.reconnecting = state.connectedOnce;
            setConnectionState('reconnecting', '재연결 중');
        };
    }

    function applyPaymentEvent(event) {
        const payment = state.payments.find(item => item.id === event.paymentId);
        if (!payment) return;
        payment.status = event.status;
        payment.updatedAt = event.updatedAt;
        const row = elements.tableBody.querySelector(`[data-payment-id="${event.paymentId}"]`);
        if (row) {
            row.querySelector('[data-field="status"]').innerHTML = statusBadge(event.status);
            row.querySelector('[data-field="updatedAt"]').textContent = formatDate(event.updatedAt);
            row.classList.remove('updated');
            requestAnimationFrame(() => row.classList.add('updated'));
        }
        if (state.selectedPaymentId === event.paymentId) loadDetail();
        clearTimeout(state.statisticsTimer);
        state.statisticsTimer = setTimeout(loadStatistics, 500);
        if (elements.searchForm.elements.status.value && elements.searchForm.elements.status.value !== event.status) {
            clearTimeout(state.refreshTimer);
            state.refreshTimer = setTimeout(() => loadPayments(false), 400);
        }
    }

    function setConnectionState(className, label) {
        elements.connectionState.className = `connection-state ${className}`;
        elements.connectionState.querySelector('.connection-label').textContent = label;
    }

    elements.searchForm.addEventListener('submit', event => {
        event.preventDefault(); state.cursorHistory = [0]; state.pageIndex = 0; loadPayments();
    });
    elements.resetButton.addEventListener('click', () => {
        elements.searchForm.reset(); state.cursorHistory = [0]; state.pageIndex = 0; loadPayments();
    });
    elements.retryButton.addEventListener('click', () => loadPayments());
    elements.refreshButton.addEventListener('click', () => Promise.all([loadPayments(false), loadStatistics()]));
    elements.previousButton.addEventListener('click', () => { if (state.pageIndex > 0) { state.pageIndex--; loadPayments(); } });
    elements.nextButton.addEventListener('click', () => {
        if (!state.hasNext || !state.payments.length) return;
        state.cursorHistory[state.pageIndex + 1] = state.payments.at(-1).id;
        state.pageIndex++;
        loadPayments();
    });
    elements.tableBody.addEventListener('click', event => {
        const button = event.target.closest('[data-action="detail"]');
        if (button) openDetail(button.dataset.paymentId);
    });
    elements.closeDrawerButton.addEventListener('click', closeDetail);
    elements.drawerBackdrop.addEventListener('click', closeDetail);
    document.addEventListener('keydown', event => { if (event.key === 'Escape') closeDetail(); });
    window.addEventListener('beforeunload', () => state.eventSource?.close());

    Promise.all([loadPayments(), loadStatistics()]).finally(connectSse);
})();
