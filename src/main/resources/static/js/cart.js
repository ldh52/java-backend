function handleMinus(cid) {
    const cartRow = document.querySelector(`[data-cid="${cid}"]`);
    const quantity = parseInt(cartRow.querySelector('.cart-quantity').innerText);
    if (quantity > 1) {
        updateCart(cid, quantity - 1);
    } else {
        alert("최소 수량은 1개입니다.");
    }
}

function handlePlus(cid) {
    const cartRow = document.querySelector(`[data-cid="${cid}"]`);
    const quantity = parseInt(cartRow.querySelector('.cart-quantity').innerText);
    updateCart(cid, quantity + 1);
}

function handleTrash(cid) {
    if (confirm("정말 삭제하시겠습니까?")) {
        updateCart(cid, 0); // 수량을 0으로 설정하여 삭제
    }
}

function updateCart(cid, quantity) {
    fetch('/mall/updateCart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ cid, quantity }),
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // UI 업데이트
            document.querySelector('#totalPrice').value = data.totalPrice.toLocaleString();
            document.querySelector('#deliveryCost').value = data.deliveryCost.toLocaleString();
            document.querySelector('#totalPriceIncludingDeliveryCost').value = data.totalPriceIncludingDeliveryCost.toLocaleString();

            const cartRow = document.querySelector(`[data-cid="${cid}"]`);
            if (quantity === 0) {
                cartRow.remove(); // 아이템 삭제
            } else {
                cartRow.querySelector('.cart-quantity').innerText = quantity;
                cartRow.querySelector('.cart-subtotal').innerText = data.subTotal.toLocaleString();
            }
        } else {
            alert("카트 업데이트 중 오류가 발생했습니다.");
        }
    })
    .catch(error => console.error("Error:", error));
}

function openPostcodePopup() {
    new daum.Postcode({
        oncomplete: function(data) {
            const address = data.roadAddress ? data.roadAddress : data.jibunAddress;
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById('address').value = address;
        }
    }).open();
}

function handleOrder() {
    const clientKey = document.getElementById("TOSS_CLIENT_KEY").value;     // Toss Payments Client Key (테스트 키)
    const orderId = "ORDER_" + new Date().getTime(); // 고유 주문 ID
    const totalAmount = document.getElementById("totalPriceIncludingDeliveryCost").value.replace(/,/g, ""); // 총 결제 금액
    const customerName = "홍길동"; // 사용자 이름 (동적으로 변경 가능)
    const successUrl = "http://localhost:8090/payment/success";     // 성공시 리다이렉트 URL
    const failureUrl = "http://localhost:8090/payment/failure";     // 실패시 리다이렉트 URL

    // Toss Payments 위젯 실행
    TossPayments(clientKey)
        .requestPayment('카드', {
            amount: parseInt(totalAmount),
            orderId: orderId,
            orderName: "장바구니 상품 결제",
            customerName: customerName,
            successUrl: successUrl,
            failUrl: failureUrl
        })
        .catch(function (error) {
            if (error.code === 'USER_CANCEL') {
                alert('결제가 취소되었습니다.');
            } else {
                alert('결제 중 오류가 발생했습니다: ' + error.message);
            }
        });
}