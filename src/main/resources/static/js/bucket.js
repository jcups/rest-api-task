"use strict";

function loadBucket() {
    sendGet("/api/bucket", () => {
        let bucket = new Bucket(JSON.parse(xhr.response));
        let items = bucket.items;
        for (let item of items) {
            drawItemInBucket(item);
            recalcSum("quantity_" + item.id);
        }
    })

    function drawItemInBucket(item) {
        let previousItemIfExists = document.getElementById("quantity_" + item.id);
        if (previousItemIfExists == null) {
            let bucket = document.getElementById('bucket');
            let tableRowElement = document.createElement('tr');
            tableRowElement.className = "row";
            let colImage = document.createElement('td');
            colImage.className = "col-2 text-center";
            let image = document.createElement('img');
            image.src = item.titleImageUrl;
            image.alt = "image";
            image.height = 125;
            colImage.appendChild(image);
            tableRowElement.appendChild(colImage);
            let colTitle = document.createElement('td');
            colTitle.className = "col-3 align-self-center ";
            colTitle.innerText = item.title;
            tableRowElement.appendChild(colTitle);
            let colQuantity = document.createElement('td');
            colQuantity.className = "col-1 align-self-center ";
            let quantityDivElement = document.createElement('div');
            quantityDivElement.className = "input-group mb-3";
            let quantityInput = document.createElement('input');
            quantityInput.className = "border form-control-plaintext text-center";
            quantityInput.id = "quantity_" + item.id;
            quantityInput.type = 'number';
            quantityInput.value = '1';
            quantityInput.placeholder = 'quantity';
            quantityInput.onchange = () => recalcSum(quantityInput.id);
            quantityInput.onsubmit = () => recalcSum(quantityInput.id);
            quantityDivElement.appendChild(quantityInput);
            colQuantity.appendChild(quantityDivElement);
            tableRowElement.appendChild(colQuantity);
            let colPrice = document.createElement('td');
            colPrice.className = "col-2 align-self-center text-center";
            colPrice.id = "price_" + item.id;
            colPrice.innerText = item.price + ' BYN';
            tableRowElement.appendChild(colPrice);
            let colSum = document.createElement('td');
            colSum.className = "col-2 align-self-center text-center";
            colSum.id = "sum_" + item.id;
            colSum.innerText = "sum BYN";
            tableRowElement.appendChild(colSum);
            let colActions = document.createElement('td');
            colActions.className = "col-2 align-self-center text-center";
            let actionsDiv = document.createElement('div');
            actionsDiv.className = "btn-group";
            actionsDiv.role = "group";
            let infoButton = document.createElement('button');
            infoButton.className = "btn btn-primary";
            infoButton.type = 'button';
            infoButton.innerText = "Info";
            infoButton.onclick = () => ref('/market/' + item.id);
            actionsDiv.appendChild(infoButton);
            let deleteButton = document.createElement('button');
            deleteButton.className = "btn btn-primary";
            deleteButton.type = 'button';
            deleteButton.innerText = 'Delete';
            deleteButton.onclick = () => sendDelete('/api/bucket/' + item.id, () => {
                if (xhr.response.status === 200){
                    console.log('successful delete');
                }
            });
            actionsDiv.appendChild(deleteButton)
            colActions.appendChild(actionsDiv);
            tableRowElement.appendChild(colActions)
            bucket.appendChild(tableRowElement);
        } else {
            previousItemIfExists.value = parseInt(previousItemIfExists.value) + 1;
        }
    }
}

function recalcSum(quantity_id) {
    let inputQuantity = document.getElementById(quantity_id);
    let value = inputQuantity.value;
    let sum_id = quantity_id.replaceAll("quantity_", "sum_");
    let price_id = sum_id.replaceAll("sum_", "price_");
    document.getElementById(sum_id).innerText =
        (parseFloat(document.getElementById(price_id)
            .innerText.replaceAll(" BYN", "")) * value) + ' BYN';
    recalcTotalSum();
}

function recalcTotalSum() {
    let bucket = document.getElementById('bucket');
    let rows = bucket.children;
    let totalSum = 0;
    let totalItems = 0;
    for (let row of rows) {
        let inputQuantity = parseInt(row.children[2].firstChild.firstChild.value);
        let price = parseFloat(row.children[3].innerHTML.replaceAll(" BYN", ""));
        totalItems = totalItems+inputQuantity;
        totalSum = totalSum+(inputQuantity*price);
    }
    document.getElementById('totalSum').innerText = totalSum;
    document.getElementById('totalCount').innerText = totalItems;
}

class Bucket {
    constructor(json) {
        this.id = json.id;
        this.user = json.user;
        this.items = new Set();
        for (let item of json.items) {
            this.items.add(new Item(item));
        }
    }
}
