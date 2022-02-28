"use strict";

function loadData() {
    sendGet('/api/items', () => {
        console.log(xhr.status);
        if (xhr.status <= 400) {
            let arr = JSON.parse(xhr.response);
            for (const item of arr) {
                drawItemInMarket(item);
            }
        }
    });
}

function getButtons(item) {
    let buttons = document.createElement('div');
    buttons.className = "d-flex justify-content-between align-items-center";
    let btnGroup = document.createElement('div');
    btnGroup.className = "btn-group";

    let btnMore = getBtnWithNameAndAnchor("More info", () => {
        ref("/market/" + item.id)
    });
    btnGroup.appendChild(btnMore);
    let btnBucket = getBtnWithNameAndAnchor("Add to Bucket", () => {
        sendGet("/api/items/add/" + item.id, () => {
            console.log("added?")
        });
    })
    btnGroup.appendChild(btnBucket)
    buttons.appendChild(btnGroup)
    let price = document.createElement('small');
    price.className = "text-muted";
    price.innerText = item.price + ' BYN';
    buttons.appendChild(price);
    return buttons;

    function getBtnWithNameAndAnchor(name, onclick) {
        let btnMore = document.createElement('button');
        btnMore.className = "btn btn-sm btn-outline-secondary";
        btnMore.type = 'button';
        btnMore.innerText = name;
        btnMore.onclick = onclick;
        return btnMore;
    }
}

function drawItemInMarket(item) {
    let col = document.createElement('div');
    col.className = "col";
    let card = document.createElement('div');
    card.className = "card shadow-sm";
    let cardImg = document.createElement('div');
    cardImg.className = "card-img-top bg-placeholder-img text-center p-3";
    let preview = document.createElement('img');
    preview.src = item.titleImageUrl;
    preview.height = 225;
    preview.alt = "server error";
    cardImg.appendChild(preview);
    card.appendChild(cardImg)
    let cardBody = document.createElement('div');
    cardBody.className = "card-body";
    let titleHeading = document.createElement('h4');
    titleHeading.className = "card-title";
    titleHeading.innerText = item.title;
    cardBody.appendChild(titleHeading);
    let description = document.createElement('p');
    description.className = "card-text";
    description.innerText = item.description;
    cardBody.appendChild(description);
    let buttons = getButtons(item);
    cardBody.appendChild(buttons);
    card.appendChild(cardBody);
    col.appendChild(card);
    document.getElementById('items').appendChild(col)
}