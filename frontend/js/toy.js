(function () {
  const app = window.AbiToyShop;
  if (!app) {
    return;
  }

  const page = document.body?.dataset.page || "";
  const MAX_IMAGE_FILE_SIZE = 20 * 1024 * 1024;

  function escapeHtml(value) {
    return String(value ?? "")
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#39;");
  }

  function parsePriceRange(value) {
    if (!value || value === "all") {
      return [Number.NEGATIVE_INFINITY, Number.POSITIVE_INFINITY];
    }

    if (value.endsWith("+")) {
      return [Number(value.slice(0, -1)), Number.POSITIVE_INFINITY];
    }

    const parts = value.split("-").map(Number);
    return [parts[0], parts[1]];
  }

  function getCategoryForToyType(toyType) {
    const normalizedToyType = String(toyType || "").toLowerCase();
    if (normalizedToyType.includes("electronic")) {
      return "Electronic";
    }
    if (normalizedToyType.includes("soft")) {
      return "Soft";
    }
    return "General";
  }

  function updateCategoryField(form) {
    const toyTypeField = form?.querySelector('[name="toyType"]');
    const categoryField = form?.querySelector('[name="category"]');
    if (!toyTypeField || !categoryField) {
      return;
    }

    categoryField.value = getCategoryForToyType(toyTypeField.value);
  }

  function syncCategoryField(form) {
    const toyTypeField = form?.querySelector('[name="toyType"]');
    if (!toyTypeField) {
      return;
    }

    updateCategoryField(form);
    toyTypeField.addEventListener("change", function () {
      updateCategoryField(form);
    });
  }

  function sortToys(toys, sortValue) {
    const sorted = toys.slice();

    switch (sortValue) {
      case "name-asc":
        sorted.sort(function (a, b) {
          return a.name.localeCompare(b.name);
        });
        break;
      case "name-desc":
        sorted.sort(function (a, b) {
          return b.name.localeCompare(a.name);
        });
        break;
      case "price-asc":
        sorted.sort(function (a, b) {
          return Number(a.price) - Number(b.price);
        });
        break;
      case "price-desc":
        sorted.sort(function (a, b) {
          return Number(b.price) - Number(a.price);
        });
        break;
      case "newest":
        sorted.sort(function (a, b) {
          return b.toyId.localeCompare(a.toyId, undefined, { numeric: true });
        });
        break;
      case "popularity":
      default:
        sorted.sort(function (a, b) {
          return Number(b.quantity) - Number(a.quantity);
        });
        break;
    }

    return sorted;
  }

  async function initCatalogPage() {
    const searchInput = document.getElementById("searchInput");
    const categoryFilter = document.getElementById("categoryFilter");
    const priceFilter = document.getElementById("priceFilter");
    const sortFilter = document.getElementById("sortFilter");
    const clearFiltersBtn = document.getElementById("clearFiltersBtn");
    const summary = document.getElementById("catalogSummary");
    const grid = document.getElementById("catalogGrid");
    const emptyState = document.getElementById("catalogEmptyState");

    if (!grid) {
      return;
    }

    const toys = await app.loadToys();

    function applyFilters() {
      const keyword = (searchInput?.value || "").trim().toLowerCase();
      const [minPrice, maxPrice] = parsePriceRange(priceFilter?.value || "all");
      const sortValue = sortFilter?.value || "popularity";

      const filtered = sortToys(toys.filter(function (toy) {
        const matchesKeyword = !keyword
          || toy.name.toLowerCase().includes(keyword)
          || toy.brand.toLowerCase().includes(keyword)
          || toy.toyType.toLowerCase().includes(keyword);
        const price = Number(toy.price);
        const matchesPrice = price >= minPrice && price <= maxPrice;
        return matchesKeyword && matchesPrice;
      }), sortValue);

      grid.innerHTML = filtered.map(function (toy) {
        return app.renderProductCard(toy);
      }).join("");

      const summaryParts = [`${filtered.length} toy${filtered.length === 1 ? "" : "s"} available`];
      if (!app.state.backendReachable) {
        summaryParts.push("showing demo catalog data");
      }
      if (summary) {
        summary.textContent = summaryParts.join(" | ");
      }

      emptyState?.classList.toggle("hidden", filtered.length > 0);
    }

    searchInput?.addEventListener("input", applyFilters);
    categoryFilter?.addEventListener("change", applyFilters);
    priceFilter?.addEventListener("change", applyFilters);
    sortFilter?.addEventListener("change", applyFilters);

    clearFiltersBtn?.addEventListener("click", function () {
      if (searchInput) {
        searchInput.value = "";
      }
      if (categoryFilter) {
        categoryFilter.value = "all";
      }
      if (priceFilter) {
        priceFilter.value = "all";
      }
      if (sortFilter) {
        sortFilter.value = "popularity";
      }
      applyFilters();
    });

    applyFilters();
  }

  function renderDetailInfoCard(label, value) {
    return `
      <div class="detail-info-card">
        <span>${escapeHtml(label)}</span>
        <strong>${escapeHtml(value)}</strong>
      </div>
    `;
  }

  async function initDetailsPage() {
    const container = document.getElementById("toyDetailsContainer");
    const relatedGrid = document.getElementById("relatedGrid");
    const toyId = new URLSearchParams(window.location.search).get("id");

    if (!container) {
      return;
    }

    if (!toyId) {
      container.innerHTML = `<div class="empty-state"><h3>No toy selected</h3><p>Please open this page from the catalog.</p></div>`;
      return;
    }

    const toy = await app.loadToyById(toyId);
    if (!toy) {
      container.innerHTML = `<div class="empty-state"><h3>Toy not found</h3><p>The selected product could not be loaded.</p></div>`;
      return;
    }

    const stock = app.getStockLabel(Number(toy.quantity || 0));
    container.innerHTML = `
      <article class="toy-details">
        <div class="toy-details__media">
          <img src="${app.getToyImageUrl(toy)}" alt="${escapeHtml(toy.name)}" onerror="this.src='${app.placeholderImage}'">
        </div>
        <div>
          <div class="toy-details__meta">
            <span class="meta-chip">${escapeHtml(toy.category)}</span>
            <span class="meta-chip">${escapeHtml(toy.toyType || "Toy")}</span>
          </div>
          <h2 class="toy-details__title">${escapeHtml(toy.name)}</h2>
          <p class="toy-details__price">${app.formatCurrency(toy.price)}</p>
          <div class="toy-details__polymorphism">
             <strong>Product Insight:</strong> ${escapeHtml(toy.displayMessage || "Standard toy record.")}
          </div>
          <p class="toy-details__description">${escapeHtml(toy.description || "A polished description area.")}</p>
          <div class="detail-info-grid">
            ${renderDetailInfoCard("Brand", toy.brand || "Toy Brand")}
            ${renderDetailInfoCard("Age Group", toy.ageGroup || "Kids")}
            ${renderDetailInfoCard("Stock", stock.label)}
            ${renderDetailInfoCard("Toy Type", toy.toyType)}
            ${renderDetailInfoCard("Toy ID", toy.toyId)}
          </div>
          <div class="toy-details__actions">
            <button class="btn btn--primary" type="button" data-edit-toy="${encodeURIComponent(toy.toyId)}">Edit Toy</button>
            <a class="btn btn--ghost" href="${app.routes.inventory}">Back to Inventory</a>
            <button class="btn btn--ghost" type="button" id="deleteToyBtn">Delete Toy</button>
          </div>
        </div>
      </article>
    `;

    const deleteButton = document.getElementById("deleteToyBtn");
    deleteButton?.addEventListener("click", function () {
      app.confirmAction(`Delete ${toy.name}? This action cannot be undone.`, async function () {
        try {
          await app.fetchApiJson(`${app.apiBaseUrl}/toys/${encodeURIComponent(toy.toyId)}`, {
            method: "DELETE"
          });
          app.showToast("Toy deleted successfully.", "success");
          window.setTimeout(function () {
            window.location.href = app.routes.inventory;
          }, 900);
        } catch (error) {
          app.showToast(error.message || "Toy deletion failed.", "error");
        }
      });
    });

    if (relatedGrid) {
      const toys = await app.loadToys();
      relatedGrid.innerHTML = toys
        .filter(function (item) {
          return item.toyId !== toy.toyId;
        })
        .slice(0, 3)
        .map(function (item) {
          return app.renderProductCard(item);
        })
        .join("");
    }
  }

  function clearFieldErrors(form) {
    form.querySelectorAll("[data-error-for]").forEach(function (node) {
      node.textContent = "";
    });
  }

  function showFieldErrors(form, errors) {
    Object.entries(errors).forEach(function ([field, message]) {
      const target = form.querySelector(`[data-error-for="${field}"]`);
      if (target) {
        target.textContent = message;
      }
    });
  }

  function validateToyForm(form) {
    const errors = {};
    const values = {
      toyId: form.toyId ? form.toyId.value.trim() : "",
      name: form.name.value.trim(),
      toyType: form.toyType.value.trim(),
      category: form.category ? form.category.value.trim() : "",
      brand: form.brand.value.trim(),
      price: form.price.value.trim(),
      quantity: form.quantity.value.trim(),
      ageGroup: form.ageGroup.value.trim(),
      description: form.description.value.trim()
    };

    const isEdit = form.dataset.mode === "edit" || (form.toyId && form.toyId.readOnly);
    if (isEdit && !values.toyId) {
      errors.toyId = "Toy ID is required.";
    }
    if (!values.name) {
      errors.name = "Name is required.";
    }
    if (!values.toyType) {
      errors.toyType = "Type is required.";
    }
    if (!values.brand) {
      errors.brand = "Brand is required.";
    }
    if (!values.price || Number(values.price) <= 0) {
      errors.price = "Price must be greater than 0.";
    }
    if (values.quantity === "" || Number(values.quantity) < 0) {
      errors.quantity = "Quantity must be 0 or more.";
    }
    if (!values.ageGroup) {
      errors.ageGroup = "Age group is required.";
    }

    const file = form.image.files[0];
    if (file) {
      const isValidImage = /\.(jpg|jpeg|png)$/i.test(file.name);
      if (!isValidImage) {
        errors.image = "Only JPG, JPEG, and PNG files are allowed.";
      } else if (file.size > MAX_IMAGE_FILE_SIZE) {
        errors.image = "Image must be smaller than 20 MB.";
      }
    }

    return { values: values, errors: errors };
  }

  function buildMultipartFormData(values, file) {
    const formData = new FormData();
    Object.entries(values).forEach(function ([key, value]) {
      formData.append(key, value);
    });
    if (file) {
      formData.append("image", file);
    }
    return formData;
  }

  function bindImagePreview(fileInput, previewImage, currentImageUrl) {
    if (!fileInput || !previewImage) {
      return;
    }

    if (currentImageUrl) {
      previewImage.src = currentImageUrl;
    }

    fileInput.addEventListener("change", function () {
      const file = fileInput.files[0];
      if (!file) {
        previewImage.src = currentImageUrl || app.placeholderImage;
        return;
      }

      const reader = new FileReader();
      reader.onload = function (event) {
        previewImage.src = event.target?.result || app.placeholderImage;
      };
      reader.readAsDataURL(file);
    });
  }

  async function initToyFormPage() {
    const form = document.getElementById("toyForm");
    if (!form) {
      return;
    }

    const mode = form.dataset.mode || "create";
    const previewImage = document.querySelector("#imagePreview img");
    let currentToy = null;

    if (mode === "edit") {
      const toyId = new URLSearchParams(window.location.search).get("id");
      if (!toyId) {
        app.showToast("Select a toy to edit from the catalog.", "error");
        return;
      }

      currentToy = await app.loadToyById(toyId);
      if (!currentToy) {
        app.showToast("Toy could not be loaded for editing.", "error");
        return;
      }

      form.toyId.value = currentToy.toyId || "";
      form.name.value = currentToy.name || "";
      form.toyType.value = currentToy.toyType || "General Toy";
      form.category.value = currentToy.category || "General";
      form.brand.value = currentToy.brand || "";
      form.price.value = currentToy.price || "";
      form.quantity.value = currentToy.quantity || "";
      form.ageGroup.value = currentToy.ageGroup || "";
      form.description.value = currentToy.description || "";
    }

    bindImagePreview(form.image, previewImage, currentToy ? app.getToyImageUrl(currentToy) : "");
    syncCategoryField(form);

    form.addEventListener("reset", function () {
      clearFieldErrors(form);
      window.setTimeout(function () {
        updateCategoryField(form);
        if (previewImage) {
          previewImage.src = app.placeholderImage;
        }
      }, 0);
    });

    form.addEventListener("submit", async function (event) {
      event.preventDefault();
      clearFieldErrors(form);
      updateCategoryField(form);

      const validation = validateToyForm(form);
      if (Object.keys(validation.errors).length > 0) {
        showFieldErrors(form, validation.errors);
        app.showToast("Please fix the highlighted form fields.", "error");
        return;
      }

      const file = form.image.files[0];
      const formData = buildMultipartFormData(validation.values, file);
      const isEditMode = mode === "edit";
      const endpoint = isEditMode
        ? `${app.apiBaseUrl}/toys/${encodeURIComponent(validation.values.toyId)}`
        : `${app.apiBaseUrl}/toys`;

      try {
        const payload = await app.fetchApiJson(endpoint, {
          method: isEditMode ? "PUT" : "POST",
          body: formData
        });
        const savedToyId = payload?.data?.toyId || validation.values.toyId;
        app.showToast(payload?.message || "Toy saved successfully.", "success");
        window.setTimeout(function () {
          window.location.href = app.routes.inventoryDetails(savedToyId);
        }, 900);
      } catch (error) {
        if (error.details && typeof error.details === "object" && !Array.isArray(error.details)) {
          showFieldErrors(form, error.details);
        }
        app.showToast(error.message || "Toy could not be saved.", "error");
      }
    });
  }

  async function initPage() {
    if (page === "toys") {
      await initCatalogPage();
    }
    if (page === "details") {
      await initDetailsPage();
    }
    if (page === "add" || page === "edit") {
      await initToyFormPage();
    }

    document.addEventListener("click", async function (event) {
      const editBtn = event.target.closest("[data-edit-toy]");
      if (editBtn) {
        event.preventDefault();
        const toyId = editBtn.dataset.editToy;
        await openEditModal(toyId);
      }
    });
  }

  async function openEditModal(toyId) {
    const toy = await app.loadToyById(toyId);
    if (!toy) {
      app.showToast("Could not load toy data.", "error");
      return;
    }

    const editFormHtml = `
      <div class="form-card">
        <div class="form-card__header">
          <h2>Update Product Record</h2>
          <p>Update inventory details and stock levels for "${escapeHtml(toy.name)}".</p>
        </div>
        <form id="toyModalForm" data-mode="edit">
          <div class="form-grid">
            <label class="field-group">
              <span>Toy ID</span>
              <input class="input-field" name="toyId" type="text" value="${escapeHtml(toy.toyId)}" readonly>
            </label>
            <label class="field-group">
              <span>Name</span>
              <input class="input-field" name="name" type="text" value="${escapeHtml(toy.name)}" required>
              <small class="field-error" data-error-for="name"></small>
            </label>
            <label class="field-group">
               <span>Toy Type</span>
               <select class="input-field" name="toyType" required>
                 <option value="General Toy" ${toy.toyType === "General Toy" ? "selected" : ""}>General Toy</option>
                 <option value="Electronic Toy" ${toy.toyType === "Electronic Toy" ? "selected" : ""}>Electronic Toy</option>
                 <option value="Soft Toy" ${toy.toyType === "Soft Toy" ? "selected" : ""}>Soft Toy</option>
               </select>
               <input type="hidden" name="category" value="${escapeHtml(toy.category || "General")}">
               <small class="field-error" data-error-for="toyType"></small>
            </label>
            <label class="field-group">
              <span>Brand</span>
              <select class="input-field" name="brand" required>
                <option value="Phantom" ${toy.brand === "Phantom" ? "selected" : ""}>Phantom</option>
                <option value="SoftTouch" ${toy.brand === "SoftTouch" ? "selected" : ""}>SoftTouch</option>
                <option value="Nitro" ${toy.brand === "Nitro" ? "selected" : ""}>Nitro</option>
                <option value="LEGO" ${toy.brand === "LEGO" ? "selected" : ""}>LEGO</option>
                <option value="Graceful" ${toy.brand === "Graceful" ? "selected" : ""}>Graceful</option>
                <option value="Harmony" ${toy.brand === "Harmony" ? "selected" : ""}>Harmony</option>
                <option value="Other" ${!["Phantom", "SoftTouch", "Nitro", "LEGO", "Graceful", "Harmony"].includes(toy.brand) ? "selected" : ""}>Other</option>
              </select>
              <small class="field-error" data-error-for="brand"></small>
            </label>
            <label class="field-group">
              <span>Price (Rs.)</span>
              <input class="input-field" name="price" type="number" step="0.01" value="${toy.price}" required>
              <small class="field-error" data-error-for="price"></small>
            </label>
            <label class="field-group">
              <span>Quantity</span>
              <input class="input-field" name="quantity" type="number" value="${escapeHtml(toy.quantity)}" required>
              <small class="field-error" data-error-for="quantity"></small>
            </label>
            <label class="field-group field-group--full">
              <span>Age Group</span>
              <input class="input-field" name="ageGroup" type="text" value="${escapeHtml(toy.ageGroup)}" required>
              <small class="field-error" data-error-for="ageGroup"></small>
            </label>
            <label class="field-group field-group--full">
              <span>Description</span>
              <textarea class="input-field input-field--textarea" name="description">${escapeHtml(toy.description)}</textarea>
              <small class="field-error" data-error-for="description"></small>
            </label>
            <label class="field-group field-group--full">
              <span>Replace Image (Optional)</span>
              <input class="input-field" name="image" type="file" accept=".jpg,.jpeg,.png">
              <small class="field-help">Leave this empty if the current photo is correct.</small>
              <small class="field-error" data-error-for="image"></small>
            </label>
          </div>
          <div class="form-actions">
            <button class="btn btn--primary" type="submit">Save Changes</button>
            <button class="btn btn--ghost" type="button" onclick="AbiToyShop.toggleModal(false)">Cancel</button>
          </div>
        </form>
      </div>
    `;

    app.toggleModal(true, editFormHtml);
    const form = document.getElementById("toyModalForm");
    syncCategoryField(form);

    form.addEventListener("submit", async function (event) {
      event.preventDefault();
      clearFieldErrors(form);
      updateCategoryField(form);

      const validation = validateToyForm(form);
      if (Object.keys(validation.errors).length > 0) {
        showFieldErrors(form, validation.errors);
        app.showToast("Please check the form for errors.", "error");
        return;
      }

      const formData = new FormData();
      Object.entries(validation.values).forEach(function ([key, value]) {
        formData.append(key, value);
      });

      const file = form.image.files[0];
      if (file) {
        formData.append("image", file);
      }

      try {
        await app.fetchApiJson(`${app.apiBaseUrl}/toys/${encodeURIComponent(toy.toyId)}`, {
          method: "PUT",
          body: formData
        });
        app.showToast("Changes saved successfully.", "success");
        app.toggleModal(false);
        window.setTimeout(function () {
          window.location.reload();
        }, 800);
      } catch (error) {
        if (error.details && typeof error.details === "object" && !Array.isArray(error.details)) {
          showFieldErrors(form, error.details);
        }
        app.showToast(error.message || "Save failed.", "error");
      }
    });
  }

  app.ready.then(initPage);
})();
