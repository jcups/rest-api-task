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
        let buttons = getButtons();
        cardBody.appendChild(buttons);
        card.appendChild(cardBody);
        col.appendChild(card);
        document.getElementById('items').appendChild(col);

        function getButtons() {
            let buttons = document.createElement('div');
            buttons.className = "d-flex justify-content-between align-items-center";
            let btnGroup = document.createElement('div');
            btnGroup.className = "btn-group";

            let btnMore = getBtnWithNameAndAnchor("More info", () => {
                ref("/market/" + item.id)
            });
            btnGroup.appendChild(btnMore);
            let btnBucket = getBtnWithNameAndAnchor("Add to Bucket", () => addToBucket(item.id))
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

    }
}

class Item {
    constructor(json) {
        this.id = json.id;
        this.title = json.title;
        this.brand = json.brand;
        this.model = json.model;
        this.series = json.series;
        this.titleImageUrl = json.titleImageUrl;
        this.price = json.price;
        this.description = json.description;
        this.rate = json.rate;
        this.countRates = json.countRates;
        this.allImagesUrls = json.allImagesUrls;
        this.params = Object.assign(new Map(), json.params);
    }
}