const actionSelect = document.getElementById("action");
const orderInput = document.getElementById("order");

function toggleOrderIdRequirement() {
    const action = actionSelect.value;
    if (action === "adjustInventory" || action === "cancelInventory") {
        orderInput.required = false;
        orderInput.disabled = true;
        orderInput.value = "";
    } else {
        orderInput.required = true;
        orderInput.disabled = false;
    }
}

actionSelect.addEventListener("change", toggleOrderIdRequirement);

toggleOrderIdRequirement();

let inventoryData = [];

function findInventory(sku, location) {
    return inventoryData.find(item => item.sku === sku && item.location === location);
}

function loadInventoryData() {
    fetch('/Inventory-Management-System/api/getAllInventory')
        .then(response => response.json())
        .then(data => {
            const tbody = document.querySelector('#inventoryTable tbody');
            tbody.innerHTML = ''; // Clear old rows

            data.forEach(item => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${item.SKU}</td>
                    <td>${item.ProductID || ''}</td>
                    <td>${item.Location}</td>
                    <td>${item.Quantity || 0}</td>
                    <td>${item.OrderAllocatedQty || 0}</td>
                    <td>${item.OrderReservedQty || 0}</td>
                `;
                tbody.appendChild(row);
            });
        })
        .catch(err => {
            document.querySelector('#inventoryTable tbody').innerHTML = "<tr><td colspan='6'>Failed to load data</td></tr>";
            console.error('Failed to load inventory:', err);
        });
}

function renderTable() {
    const tbody = document.querySelector("#inventoryTable tbody");
    if (!tbody) return;
    tbody.innerHTML = "";
    inventoryData.forEach(item => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${item.sku}</td>
            <td>${item.productId || ""}</td>
            <td>${item.location}</td>
            <td>${item.availableQty}</td>
            <td>${item.allocatedQty}</td>
            <td>${item.reservedQty}</td>
        `;
        tbody.appendChild(row);
    });
}

document.getElementById("addInventoryForm").addEventListener("submit", function(e) {
    e.preventDefault();
    const sku = document.getElementById("sku_add").value.trim();
    const productId = document.getElementById("product").value.trim();
    const location = document.getElementById("location").value.trim();
    const quantity = parseInt(document.getElementById("quantity_add").value, 10);

    if (!sku || !location || isNaN(quantity)) return;

    let item = findInventory(sku, location);
    if (item) {
        item.availableQty += quantity;
    } else {
        inventoryData.push({
            sku,
            productId,
            location,
            availableQty: quantity,
            reservedQty: 0,
            allocatedQty: 0
        });
    }
    renderTable();
    this.reset();
});

document.getElementById("inventoryActionsForm").addEventListener("submit", function(e) {
    e.preventDefault();
    const action = document.getElementById("action").value;
    const sku = document.querySelector("#inventoryActionsForm input[name='sku_name']").value.trim();
    const quantity = parseInt(document.querySelector("#inventoryActionsForm input[name='quantity']").value, 10);
    let item = inventoryData.find(i => i.sku === sku);

    if (!item || isNaN(quantity)) return;

    switch (action) {
        case "adjustInventory":
            item.availableQty += quantity;
            break;
        case "cancelInventory":
            item.availableQty = Math.max(0, item.availableQty - quantity);
            break;
        case "allocateInventory":
            if (item.availableQty >= quantity) {
                item.availableQty -= quantity;
                item.allocatedQty += quantity;
            } else {
                alert("Not enough available inventory to allocate.");
            }
            break;
        case "reserveInventory":
            if (item.allocatedQty >= quantity) {
                item.allocatedQty -= quantity;
                item.reservedQty += quantity;
            } else {
                alert("Not enough allocated inventory to reserve.");
            }
            break;
    }
    renderTable();
    this.reset();
});

renderTable();

loadInventoryData();
