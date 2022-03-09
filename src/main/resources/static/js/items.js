"use strict";

function loadMarket(pageNum) {
    cleanPage()
    let page = getPageItems(pageNum);
    console.log(page)
    let items = page.content;
    console.log(items)
    for (let item of items) {
        drawItemInMarket(item);
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

    loadNav(page,
        () => loadMarket(page.number - 1),
        () => loadMarket(parseInt(page.number)),
        () => loadMarket(page.number + 1))
}

function cleanPage() {
//            <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3" id="items"></div>
    let itemsLast = document.getElementById('items');
    let parentElement = itemsLast.parentElement;
    itemsLast.remove();
    parentElement.insertAdjacentHTML('afterbegin',
        "<div class=\"row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3\" id=\"items\"></div>");
    let pageNav = document.getElementById('pageable');
    let navParent = pageNav.parentElement;
    pageNav.remove();
    let navUl = document.createElement('ul');
    navUl.className = 'pagination justify-content-center';
    navUl.id = 'pageable';
    navParent.append(navUl);
}

function getPageItems(page) {
    xhr = new XMLHttpRequest();
    xhr.open("GET", origin + "/api/items?size=9&page=" + page, false)
    xhr.onload = () => {
        console.log(xhr.status);
    }
    xhr.send();
    return JSON.parse(xhr.response);
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