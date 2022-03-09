"use strict";

function loadUsers(pageNum) {
    cleanPage();
    let page = getPageUsers(pageNum);
    let users = page.content;
    let roles = getRoles();

    if (users == null) {
        alert("users == null")
        return;
    }

    function createBodyCol(text, className) {
        let col = createElementByTagAndClassName('td', className);
        col.innerText = text;
        return col;
    }

    function createHeadingCol(className, text) {
        let headingCol = createElementByTagAndClassName('th', className);
        headingCol.innerText = text;
        return headingCol;
    }

    function getRolesCol(user) {
        let roles = user.roles;
        let rolesCol = createElementByTagAndClassName('td', 'col-3');

        function createDDButton(roleName) {
            return "<button aria-expanded='false' class='btn btn-secondary btn-sm dropdown-toggle m-1'" +
                " data-bs-toggle='dropdown' id='dd_" + roleName + '_' + user.id + "' type='button'>" + roleName + "</button>";
        }

        function createDDUl(roleName) {
            return "<ul aria-labelledby='dd_" + roleName + '_' + user.id + "' class='dropdown-menu'></ul>";
        }

        function createDeleteRoleButton(roleName) {
            return createDropdownItemWithInnerHTML("<button class='dropdown-item small' " +
                "onclick=\"deleteRole('" + roleName + "', " + user.id + ")\">Delete</button>");
        }

        function userHasRole(roleName) {
            return roles.find(role => role === roleName) != null;
        }

        function insertDefault(roleName) {
            rolesCol.insertAdjacentHTML('beforeend', createDDButton(roleName));
            rolesCol.insertAdjacentHTML('beforeend', createDDUl(roleName));
        }

        if (roles.length >= 1) {
            if (userHasRole("ROLE_USER")) {
                let roleName = "USER";
                insertDefault(roleName);
                let ulElem = rolesCol.lastChild;
                ulElem.append(createDeleteRoleButton(roleName));
            }
            if (userHasRole("ROLE_MODERATOR")) {
                let roleName = "MODERATOR";
                insertDefault(roleName);
                let ulElem = rolesCol.lastChild;
                ulElem.append(createDropdownItemWithInnerHTML(
                    "<a class='dropdown-item small' href='/moderator' target='_blank'>Dashboard</a>"))
                ulElem.append(createDeleteRoleButton(roleName));
            }
            if (userHasRole("ROLE_ADMIN")) {
                let roleName = "ADMIN";
                insertDefault(roleName);
                let ulElem = rolesCol.lastChild;
                ulElem.append(createDropdownItemWithInnerHTML(
                    "<a class='dropdown-item small' href='/admin' target='_blank'>Dashboard</a>"));
                ulElem.append(createDeleteRoleButton(roleName));
            }
            if (userHasRole("ROLE_CREATOR")) {
                let roleName = "CREATOR";
                insertDefault(roleName);
                let ulElem = rolesCol.lastChild;
                ulElem.append(createDropdownItemWithInnerHTML(
                    "<a class='dropdown-item small' href='/creator' target='_blank'>Dashboard</a>"))
                ulElem.append(createDeleteRoleButton(roleName));
            }
        }

        let appendButton = "<button aria-expanded='false' class='btn btn-secondary btn-sm  m-1'" +
            " data-bs-toggle='dropdown' id='dd_APPEND_" + user.id + "' type='button'>" +
            "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-plus-circle\" viewBox=\"0 0 16 16\">\n" +
            "  <path d=\"M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z\"></path>\n" +
            "  <path d=\"M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z\"></path>\n" +
            "</svg></button>"
        rolesCol.insertAdjacentHTML('beforeend', appendButton);
        rolesCol.insertAdjacentHTML('beforeend', "<ul aria-labelledby='dd_APPEND_" + user.id + "' class='dropdown-menu'></ul>")
        let ulElem = rolesCol.lastChild;

        function createInnerHTMLToAppendButtonWithRole(roleName) {
            return "<button class='dropdown-item small' " +
                "onclick=\"appendRole('" + roleName + "', " + user.id + ")\">" + roleName + "</button>";
        }

        ulElem.append(createDropdownItemWithInnerHTML(
            createInnerHTMLToAppendButtonWithRole("USER")));

        ulElem.append(createDropdownItemWithInnerHTML(
            createInnerHTMLToAppendButtonWithRole("MODERATOR")));

        ulElem.append(createDropdownItemWithInnerHTML(
            createInnerHTMLToAppendButtonWithRole("ADMIN")));

        return rolesCol;
    }

    function hasRole(_role) {
        return roles.find(role => role === "ROLE_" + _role) != null;
    }

    function hasAnyRole(_roles) {
        for (let _role of _roles) {
            if (hasRole(_role))
                return true;
        }
        return false;
    }

    function createDropdownItemWithInnerHTML(html) {
        let liElement = document.createElement('li');
        liElement.innerHTML = html;
        return liElement;
    }

    function getActionsCol(user) {
        let actionsCol = createElementByTagAndClassName('td', 'col-1');
        let buttons = createElementByTagAndClassName('div', 'dropdown');
        buttons.insertAdjacentHTML('afterbegin', "<button class='btn btn-secondary dropdown-toggle' " +
            "type='button' id='dropdownActions_" + user.id + "' " +
            "data-bs-toggle='dropdown' aria-expanded='false'>Actions</button>")
        buttons.insertAdjacentHTML('beforeend', "<ul class='dropdown-menu' " +
            "aria-labelledby='dropdownActions_" + user.id + "'></ul>")
        let ulElement = buttons.lastChild;

        let infoActionInnerHTML = "<a class='dropdown-item small' href='/users/" + user.id + "' target='_blank'>Info</a>";
        ulElement.appendChild(createDropdownItemWithInnerHTML(infoActionInnerHTML));

        let bucketActionInnerHTML = "<a class='dropdown-item small' href='/users/" + user.id + "/bucket' target='_blank'>Bucket</a>";
        ulElement.appendChild(createDropdownItemWithInnerHTML(bucketActionInnerHTML));

        let ordersActionInnerHTML = "<a class='dropdown-item small' href='/users/" + user.id + "/orders' target='_blank'>Orders</a>";
        ulElement.appendChild(createDropdownItemWithInnerHTML(ordersActionInnerHTML));

        ulElement.appendChild(createDropdownItemWithInnerHTML("<hr class='dropdown-divider small'>"));

        let deleteActionInnerHTML = "<button class='dropdown-item small" + (hasAnyRole(['ADMIN', 'CREATOR']) ?
            "' onclick='deleteUser(" + user.id + ")" : " disabled") + "'>Delete</button>";
        ulElement.append(createDropdownItemWithInnerHTML(deleteActionInnerHTML))

        buttons.append(ulElement);
        actionsCol.append(buttons)
        return actionsCol;
    }

    function appendHeaders() {
        let usersHead = document.getElementById('usersHead');
        let head = createElementByTagAndClassName('tr', 'row');

        head.append(createHeadingCol("col-1", "ID"));
        head.append(createHeadingCol("col-1", "First name"));
        head.append(createHeadingCol("col-1", "Last name"));
        head.append(createHeadingCol("col-1", "Age"));
        head.append(createHeadingCol("col-2", "Username"));
        head.append(createHeadingCol("col-2", "Email"));
        head.append(createHeadingCol("col-1", "Actions"));
        head.append(createHeadingCol("col-3", "Roles"));
        usersHead.append(head);
    }

    let usersBody = document.getElementById('usersBody');
    appendHeaders();

    for (let user of users) {
        let rowElement = createElementByTagAndClassName('tr', "row");
        rowElement.append(createBodyCol(user.id, "col-1"));
        rowElement.append(createBodyCol(user.firstName, "col-1"));
        rowElement.append(createBodyCol(user.lastName, "col-1"));
        rowElement.append(createBodyCol(user.age, "col-1 text-center"));
        rowElement.append(createBodyCol(user.username, "col-2"));
        rowElement.append(createBodyCol(user.email, "col-2"));
        rowElement.append(getActionsCol(user))
        rowElement.append(getRolesCol(user));
        usersBody.append(rowElement);
    }
    loadNav(page,
        () => loadUsers(page.number - 1),
        () => loadUsers(parseInt(page.number)),
        () => loadUsers(page.number + 1));
}

function cleanPage() {
    let usersHead = document.getElementById('usersHead');
    let tableParent = usersHead.parentElement;
    usersHead.remove();
    let tHead = document.createElement('thead');
    tHead.id = 'usersHead';
    tableParent.append(tHead);
    let usersBody = document.getElementById('usersBody');
    usersBody.remove();
    let tBody = document.createElement('tbody');
    tBody.id = 'usersBody';
    tableParent.append(tBody);
    let pageNav = document.getElementById('pageable');
    let navParent = pageNav.parentElement;
    pageNav.remove();
    let navUl = document.createElement('ul');
    navUl.className = 'pagination justify-content-center';
    navUl.id = 'pageable';
    navParent.append(navUl);
}

function getRoles() {
    xhr = new XMLHttpRequest();
    xhr.open('GET', origin + "/api/users/roles", false)
    xhr.onload = () => {
        console.log(xhr.status)
    }
    xhr.send();
    return JSON.parse(xhr.response);
}

function appendRole(roleName, userId) {
    xhr = new XMLHttpRequest();
    xhr.open("PATCH", origin + "/api/users/" + userId + "/roles");
    xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
    xhr.send(JSON.stringify({
        "role": 'ROLE_' + roleName,
        "action": "APPEND"
    }))
}

function deleteRole(roleName, userId) {
    xhr = new XMLHttpRequest();
    xhr.open("PATCH", origin + "/api/users/" + userId + "/roles");
    xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
    xhr.send(JSON.stringify({
        "role": 'ROLE_' + roleName,
        "action": "DELETE"
    }))
}

function getPageUsers(page) {
    xhr = new XMLHttpRequest();
    xhr.open("GET", origin + "/api/users?page=" + page, false);
    xhr.onload = () => {
        console.log(xhr.status)
    }
    xhr.send();
    return JSON.parse(xhr.response);
}
