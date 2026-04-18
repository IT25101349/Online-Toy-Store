(function () {
  const body = document.body;
  const root = body?.dataset.root || ".";
  const page = body?.dataset.page || "";
  const apiBaseUrl = resolveApiBaseUrl();
  const placeholderImage = `${root}/assets/images/toy-placeholder.svg`;
  const pageBase = `${root}/inventory`;
  const routes = {
    home: `${root}/index.html`,
    inventory: `${root}/inventory`,
    addToy: `${root}/inventory/add`,
    inventoryDetails: function (toyId) {
      return `${root}/inventory/details?id=${encodeURIComponent(toyId)}`;
    },
    inventoryEdit: function (toyId) {
      return `${root}/inventory/edit?id=${encodeURIComponent(toyId)}`;
    }
  };

  const fallbackToys = [
    {
      toyId: "TSR001",
      name: "Electronic Drone",
      category: "Electronic",
      brand: "Phantom",
      price: 12500.0,
      quantity: 15,
      ageGroup: "8+",
      description: "High speed racing drone with 4K camera.",
      imageFileName: "drone.jpg",
      imageUrl: "",
      toyType: "Electronic Toy"
    },
    {
      toyId: "TSR002",
      name: "Plush Elephant",
      category: "Soft",
      brand: "SoftTouch",
      price: 2800.0,
      quantity: 30,
      ageGroup: "0+",
      description: "Soft cuddly elephant toy for toddlers.",
      imageFileName: "elephant.jpg",
      imageUrl: "",
      toyType: "Soft Toy"
    },
    {
      toyId: "TSR003",
      name: "Remote Control Car",
      category: "Electronic",
      brand: "Nitro",
      price: 8500.0,
      quantity: 12,
      ageGroup: "6+",
      description: "Off-road remote control car with suspension.",
      imageFileName: "car.jpg",
      imageUrl: "",
      toyType: "Electronic Toy"
    },
    {
      toyId: "TSR004",
      name: "Building Castle",
      category: "General",
      brand: "LEGO",
      price: 15000.0,
      quantity: 10,
      ageGroup: "7+",
      description: "750-piece castle building blocks set.",
      imageFileName: "castle.jpg",
      imageUrl: "",
      toyType: "General Toy"
    },
    {
      toyId: "TSR005",
      name: "Talking Doll",
      category: "Electronic",
      brand: "Graceful",
      price: 4200.0,
      quantity: 20,
      ageGroup: "3+",
      description: "Educational talking doll with 50+ phrases.",
      imageFileName: "doll.jpg",
      imageUrl: "",
      toyType: "Electronic Toy"
    },
    {
      toyId: "TSR006",
      name: "Musical Bear",
      category: "Soft",
      brand: "Harmony",
      price: 3500.0,
      quantity: 18,
      ageGroup: "1+",
      description: "Soft bear that plays soothing lullabies.",
      imageFileName: "bear.jpg",
      imageUrl: "",
      toyType: "Soft Toy"
    }
  ];

  const componentFallbacks = {
    "navbar.html": `
      <nav class="site-navbar">
        <div class="nav-shell container">
          <a class="brand-mark" href="{{ROOT}}/index.html" aria-label="Abi Toy Inventory home">
            <span class="brand-mark__icon" aria-hidden="true">
              <span class="dot dot--blue"></span>
              <span class="dot dot--yellow"></span>
              <span class="dot dot--coral"></span>
            </span>
            <span class="brand-mark__text">
              <strong>Abi Toy Inventory</strong>
              <small>Simple toy stock management dashboard</small>
            </span>
          </a>
          <button class="mobile-nav-toggle" type="button" aria-expanded="false" aria-label="Toggle navigation">
            <span></span>
            <span></span>
            <span></span>
          </button>
          <div class="nav-panel">
            <ul class="nav-links">
              <li><a data-nav-link="home" href="{{ROOT}}/index.html">Home</a></li>
              <li><a data-nav-link="toys" href="{{ROOT}}/inventory">Inventory</a></li>
              <li><a data-nav-link="categories" href="{{ROOT}}/inventory#categories">Categories</a></li>
              <li><a data-nav-link="add" href="{{ROOT}}/inventory/add">Add Toy</a></li>
            </ul>
            <div class="nav-actions">
              <a class="nav-search" href="{{ROOT}}/inventory" aria-label="Search toys">
                <span class="nav-icon">Search</span>
                <span>Find Toy</span>
              </a>
            </div>
          </div>
        </div>
      </nav>
    `,
    "footer.html": `
      <footer class="site-footer" id="footer-contact">
        <div class="container footer-grid">
          <div>
            <h3>Abi Toy Inventory</h3>
            <p>A professional toy inventory management system designed for efficient stock tracking and business operations.</p>
          </div>
          <div>
            <h4>Quick Links</h4>
            <ul class="footer-links">
              <li><a href="{{ROOT}}/index.html">Home</a></li>
              <li><a href="{{ROOT}}/inventory">Inventory</a></li>
              <li><a href="{{ROOT}}/inventory/add">Add Toy</a></li>
            </ul>
          </div>
          <div>
            <h4>Inventory Actions</h4>
            <ul class="footer-links">
              <li><a href="{{ROOT}}/inventory">Search and filter toys</a></li>
              <li><a href="{{ROOT}}/inventory/add">Create inventory records</a></li>
              <li>Update and delete stock items</li>
            </ul>
          </div>
          <div>
            <div class="social-row">
              <a href="#">Help Center</a>
              <a href="#">Inventory Guide</a>
              <a href="#">Contact Us</a>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <div class="container footer-bottom__row">
            <span>Inventory management for toy records, images, and stock visibility.</span>
            <span>&copy; <span data-current-year></span> Abi Toy Inventory</span>
          </div>
        </div>
      </footer>
    `
  };

  const state = {
    backendReachable: true,
    toysCache: null,
    modalActive: false
  };

  function getBaseOrigin() {
    if (window.location.protocol === "http:" || window.location.protocol === "https:") {
      return window.location.origin;
    }
    return "http://localhost:8081";
  }

  function resolveApiBaseUrl() {
    if (window.ABI_TOY_API_BASE) {
      return window.ABI_TOY_API_BASE;
    }
    return `${getBaseOrigin()}/api`;
  }

  function resolvePath(path) {
    return `${root}/${path}`;
  }

  function escapeHtml(value) {
    return String(value ?? "")
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#39;");
  }

  function formatCurrency(value) {
    const numericValue = Number(value || 0);
    return "Rs. " + numericValue.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }

  function getToyImageUrl(toy) {
    if (toy?.imageUrl) {
      return toy.imageUrl.startsWith("http") ? toy.imageUrl : `${getBaseOrigin()}${toy.imageUrl}`;
    }
    if (toy?.imageFileName) {
      return `${apiBaseUrl}/toys/image/${encodeURIComponent(toy.imageFileName)}`;
    }
    return placeholderImage;
  }

  function getStockLabel(quantity) {
    if (quantity <= 0) {
      return { label: "Out of stock", className: "stock-badge stock-badge--out" };
    }
    if (quantity <= 5) {
      return { label: `Low stock: ${quantity} left`, className: "stock-badge stock-badge--low" };
    }
    return { label: `In stock: ${quantity}`, className: "stock-badge" };
  }

  function getCategoryBadge(category) {
    return category || "Toy";
  }

  async function loadComponent(selector, fileName) {
    const slot = document.querySelector(selector);
    if (!slot) {
      return;
    }

    const path = resolvePath(`components/${fileName}`);
    let markup = componentFallbacks[fileName] || "";

    try {
      const response = await fetch(path);
      if (response.ok) {
        markup = await response.text();
      }
    } catch (error) {
      markup = componentFallbacks[fileName] || "";
    }

    slot.innerHTML = markup.replaceAll("{{ROOT}}", root);
  }

  function setCurrentYear() {
    document.querySelectorAll("[data-current-year]").forEach((node) => {
      node.textContent = String(new Date().getFullYear());
    });
  }

  function setActiveNavLink() {
    const navKeyByPage = {
      home: "home",
      toys: "toys",
      details: "toys",
      add: "add",
      edit: "toys"
    };

    const activeKey = navKeyByPage[page];
    if (!activeKey) {
      return;
    }

    document.querySelectorAll("[data-nav-link]").forEach((link) => {
      if (link.getAttribute("data-nav-link") === activeKey) {
        link.classList.add("is-active");
      }
    });
  }

  function initMobileNav() {
    const toggleButton = document.querySelector(".mobile-nav-toggle");
    const navPanel = document.querySelector(".nav-panel");
    if (!toggleButton || !navPanel) {
      return;
    }

    toggleButton.addEventListener("click", function () {
      const expanded = toggleButton.getAttribute("aria-expanded") === "true";
      toggleButton.setAttribute("aria-expanded", String(!expanded));
      navPanel.classList.toggle("is-open");
    });
  }

  function ensureToastStack() {
    let stack = document.querySelector(".toast-stack");
    if (!stack) {
      stack = document.createElement("div");
      stack.className = "toast-stack";
      document.body.appendChild(stack);
    }
    return stack;
  }

  function showToast(message, type) {
    const stack = ensureToastStack();
    const toast = document.createElement("div");
    toast.className = `toast toast--${type || "success"}`;
    toast.textContent = message;
    stack.appendChild(toast);

    window.setTimeout(function () {
      toast.remove();
    }, 3200);
  }

  function toggleModal(show, content = "") {
    let modal = document.getElementById("globalModal");
    if (!modal) {
      modal = document.createElement("div");
      modal.id = "globalModal";
      modal.className = "modal-overlay hidden";
      modal.innerHTML = `
        <div class="modal-container">
          <button class="modal-close" id="modalCloseBtn" aria-label="Close modal">&times;</button>
          <div id="modalContent"></div>
        </div>
      `;
      document.body.appendChild(modal);
      modal.querySelector("#modalCloseBtn").onclick = () => toggleModal(false);
      modal.onclick = (e) => { if (e.target === modal) toggleModal(false); };
    }

    const contentArea = modal.querySelector("#modalContent");
    if (show) {
      contentArea.innerHTML = content;
      modal.classList.remove("hidden");
      document.body.style.overflow = "hidden";
      state.modalActive = true;
    } else {
      modal.classList.add("hidden");
      document.body.style.overflow = "";
      state.modalActive = false;
    }
  }

  function confirmAction(message, onConfirm) {
    const content = `
      <div class="confirm-dialog">
        <h3 class="confirm-title">Confirmation Required</h3>
        <p class="confirm-message">${escapeHtml(message)}</p>
        <div class="confirm-actions">
          <button class="btn btn--primary" id="confirmOk">Confirm</button>
          <button class="btn btn--ghost" id="confirmCancel">Cancel</button>
        </div>
      </div>
    `;
    toggleModal(true, content);
    document.getElementById("confirmOk").onclick = () => {
      toggleModal(false);
      onConfirm();
    };
    document.getElementById("confirmCancel").onclick = () => toggleModal(false);
  }

  async function fetchApiJson(url, options) {
    const response = await fetch(url, options);
    const isJson = response.headers.get("content-type")?.includes("application/json");
    const payload = isJson ? await response.json() : null;

    if (!response.ok) {
      const error = new Error(payload?.message || "Request failed.");
      error.details = payload?.data || null;
      error.status = response.status;
      throw error;
    }

    return payload;
  }

  async function loadToys(forceRefresh) {
    if (state.toysCache && !forceRefresh) {
      return state.toysCache;
    }

    try {
      const payload = await fetchApiJson(`${apiBaseUrl}/toys`);
      const toys = Array.isArray(payload?.data) ? payload.data : [];
      state.backendReachable = true;
      state.toysCache = toys;
      return toys;
    } catch (error) {
      state.backendReachable = false;
      state.toysCache = fallbackToys.slice();
      return state.toysCache;
    }
  }

  async function loadToyById(toyId) {
    try {
      const payload = await fetchApiJson(`${apiBaseUrl}/toys/${encodeURIComponent(toyId)}`);
      state.backendReachable = true;
      return payload?.data || null;
    } catch (error) {
      const toys = await loadToys();
      return toys.find(function (toy) {
        return toy.toyId === toyId;
      }) || null;
    }
  }

  function renderProductCard(toy, options) {
    const stock = getStockLabel(Number(toy.quantity || 0));
    const linksRoot = options?.linksRoot || pageBase;
    const detailsLink = `${linksRoot}/details?id=${encodeURIComponent(toy.toyId)}`;
    const imageSrc = getToyImageUrl(toy);

    return `
      <article class="product-card">
        <div class="product-card__media">
          <img src="${imageSrc}" alt="${escapeHtml(toy.name)}" loading="lazy" onerror="this.src='${placeholderImage}'">
          <span class="product-card__badge">${escapeHtml(getCategoryBadge(toy.category))}</span>
        </div>
        <div class="product-card__body">
          <div class="product-card__meta">
            <span>${escapeHtml(toy.brand || "Toy Brand")}</span>
            <span>${escapeHtml(toy.ageGroup || "Kids")}</span>
          </div>
          <h3 class="product-card__title">${escapeHtml(toy.name)}</h3>
          <p class="product-card__brand">${escapeHtml(toy.description || "Simple and polished toy card for your catalog UI.")}</p>
          <div class="product-card__price">${formatCurrency(toy.price)}</div>
          <span class="${stock.className}">${escapeHtml(stock.label)}</span>
          <div class="product-card__actions">
            <a class="btn btn--ghost" href="${detailsLink}">View Details</a>
            <button class="btn btn--primary" type="button" data-edit-toy="${toy.toyId}">Edit Toy</button>
          </div>
        </div>
      </article>
    `;
  }

  async function initHomePage() {
    if (page !== "home") {
      return;
    }

    const toys = await loadToys();
    const featuredGrid = document.getElementById("featured-grid");
    const popularGrid = document.getElementById("popular-grid");
    const linksRoot = `${root}/inventory`;

    if (featuredGrid) {
      featuredGrid.innerHTML = toys.slice(0, 3).map(function (toy) {
        return renderProductCard(toy, { linksRoot: linksRoot });
      }).join("");
    }

    if (popularGrid) {
      popularGrid.innerHTML = toys.slice(3, 6).map(function (toy) {
        return renderProductCard(toy, { linksRoot: linksRoot });
      }).join("");
    }
  }

  async function initSharedLayout() {
    await Promise.all([
      loadComponent("[data-site-navbar]", "navbar.html"),
      loadComponent("[data-site-footer]", "footer.html")
    ]);

    setCurrentYear();
    setActiveNavLink();
    initMobileNav();

    await initHomePage();
  }

  window.AbiToyShop = {
    apiBaseUrl: apiBaseUrl,
    pageBase: pageBase,
    routes: routes,
    placeholderImage: placeholderImage,
    loadToys: loadToys,
    loadToyById: loadToyById,
    renderProductCard: renderProductCard,
    getToyImageUrl: getToyImageUrl,
    getStockLabel: getStockLabel,
    formatCurrency: formatCurrency,
    showToast: showToast,
    toggleModal: toggleModal,
    confirmAction: confirmAction,
    fetchApiJson: fetchApiJson,
    resolvePath: resolvePath,
    state: state
  };

  window.AbiToyShop.ready = initSharedLayout();
})();
