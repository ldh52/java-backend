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
