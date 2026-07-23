// ===================== VARIABLES GLOBALES =====================
let currentUser = null;
let currentPoiId = null;
let map = null;
let markersLayer = null;
let allPois = [];

// ===================== GESTION DES TABS (W3.CSS) =====================
function openTab(evt, tabName) {
    const contents = document.getElementsByClassName("tab-content");
    for (let c of contents) c.style.display = "none";
    const links = document.getElementsByClassName("tablink");
    for (let l of links) l.className = l.className.replace(" w3-blue", "");
    document.getElementById(tabName).style.display = "block";
    evt.currentTarget.className += " w3-blue";
    // Redimensionner la carte si l'onglet "Bewertungen" est affiché
    if (tabName === "BewertungenTab" && map) {
        setTimeout(() => map.invalidateSize(), 100);
    }
}

// ===================== UI HELPER =====================
function updateUI() {
    const isLogged = currentUser !== null;
    document.getElementById("userDisplay").style.display = isLogged ? "inline" : "none";
    document.getElementById("logoutBtn").style.display = isLogged ? "inline" : "none";
    document.getElementById("deleteAccountBtn").style.display = isLogged ? "inline" : "none"; // <-- AJOUT
    document.getElementById("loginOpenBtn").style.display = isLogged ? "none" : "inline";
    document.getElementById("registerOpenBtn").style.display = isLogged ? "none" : "inline";
    document.getElementById("ratingFormContainer").style.display = isLogged ? "block" : "none";
    if (isLogged) {
        document.getElementById("usernameSpan").textContent = currentUser.username;
    }
}


// ===================== APPEL API (avec gestion texte/JSON) =====================
async function apiFetch(url, options = {}) {
    const opts = {
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        ...options
    };
    const res = await fetch(url, opts);
    if (!res.ok) {
        // Si erreur, on essaie d'abord de lire le texte (souvent un message d'erreur)
        const text = await res.text();
        throw new Error(text || res.statusText);
    }
    // On vérifie le Content-Type
    const contentType = res.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
        return res.json();
    } else {
        // Réponse en texte brut (ex: succès DELETE)
        return res.text();
    }
}

// ===================== CHARGEMENT DES POIS =====================
async function loadPois() {
    try {
        allPois = await apiFetch("/api/pois");
        initMap();
        addMarkers(allPois);
    } catch (e) {
        console.error("Fehler beim Laden der POIs:", e);
    }
}

// ===================== CARTE LEAFET =====================
function initMap() {
    if (map) return;
    map = L.map('mapContainer').setView([49.25, 7.36], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
    }).addTo(map);
    markersLayer = L.layerGroup().addTo(map);
}

function addMarkers(pois) {
    markersLayer.clearLayers();
    pois.forEach(poi => {
        if (!poi.lat || !poi.lon) return;
        const marker = L.marker([poi.lat, poi.lon])
            .addTo(markersLayer)
            .bindPopup(`<b>${poi.name || 'Unbekannt'}</b><br>${poi.amenity || ''}`);
        marker.on('click', () => {
            showPoiDetails(poi.id);
        });
    });
}

// ===================== AFFICHER LES DÉTAILS D'UN POI =====================
// ===================== AFFICHER LES DÉTAILS D'UN POI =====================
async function showPoiDetails(poiId) {
    currentPoiId = poiId;
    try {
        const poi = await apiFetch(`/api/pois/${poiId}`);
        document.getElementById("poiNameDisplay").textContent = poi.name || "Unbekannte Kneipe";

        // Adresse (on laisse toujours afficher, mais on met "-" si vide)
        const address = [poi.addrStreet, poi.addrHousenumber, poi.addrPostcode, poi.addrCity].filter(Boolean).join(", ");
        document.getElementById("poiAddress").textContent = address || "-";

        // --- Téléphone : affiché seulement s'il existe ---
        const phoneRow = document.getElementById("poiPhone").parentElement;
        if (poi.phone) {
            document.getElementById("poiPhone").textContent = poi.phone;
            phoneRow.style.display = "block";
        } else {
            phoneRow.style.display = "none";
        }

        // --- Cuisine : affichée seulement si elle existe ---
        const cuisineRow = document.getElementById("poiCuisine").parentElement;
        if (poi.cuisine) {
            document.getElementById("poiCuisine").textContent = poi.cuisine;
            cuisineRow.style.display = "block";
        } else {
            cuisineRow.style.display = "none";
        }

        // Charger les avis
        await loadRatings(poiId);
    } catch (e) {
        console.error(e);
    }
}

// ===================== CHARGER LES AVIS D'UN POI =====================
async function loadRatings(poiId) {
    try {
        const ratings = await apiFetch(`/api/ratings/poi/${poiId}`);
        const container = document.getElementById("ratingsList");
        if (ratings.length === 0) {
            container.innerHTML = "<p>Keine Bewertungen vorhanden.</p>";
            return;
        }
        let html = "";
        ratings.forEach(r => {
            const stars = "★".repeat(r.grade) + "☆".repeat(5 - r.grade);
            html += `
                <div class="rating-item">
                    <span class="rating-user">${r.username || "Unbekannt"}</span>
                    <span class="rating-stars">${stars}</span>
                    <span class="rating-date">${r.createdAt ? new Date(r.createdAt).toLocaleDateString() : ""}</span>
                    <p>${r.txt || ""}</p>
                </div>
            `;
        });
        container.innerHTML = html;
    } catch (e) {
        console.error(e);
    }
}

// ===================== GESTION DES ÉTOILES =====================
function initStars() {
    document.querySelectorAll('.star-icon').forEach(el => {
        el.addEventListener('click', function() {
            const val = parseInt(this.dataset.value);
            document.getElementById("ratingGrade").value = val;
            document.querySelectorAll('.star-icon').forEach(s => {
                s.classList.toggle('active', parseInt(s.dataset.value) <= val);
            });
        });
    });
}

// ===================== SOUMETTRE UN AVIS =====================
document.getElementById("ratingForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    if (!currentUser) {
        alert("Bitte melden Sie sich zuerst an.");
        return;
    }
    if (!currentPoiId) {
        alert("Bitte wählen Sie eine Kneipe auf der Karte aus.");
        return;
    }
    const grade = parseInt(document.getElementById("ratingGrade").value);
    const txt = document.getElementById("ratingComment").value.trim();
    if (grade === 0) {
        alert("Bitte wählen Sie eine Bewertung (1-5 Sterne).");
        return;
    }
    if (!txt) {
        alert("Bitte schreiben Sie einen Kommentar.");
        return;
    }

    const data = { poiId: currentPoiId, grade, txt, imageId: null };
    try {
        const result = await apiFetch("/api/ratings", { method: "POST", body: JSON.stringify(data) });
        alert("Bewertung erfolgreich abgegeben!");
        document.getElementById("ratingComment").value = "";
        document.getElementById("ratingGrade").value = 0;
        document.querySelectorAll('.star-icon').forEach(s => s.classList.remove('active'));
        // Aktualisieren
        await loadRatings(currentPoiId);
        await loadMyRatings();
    } catch (e) {
        alert("Fehler beim Abgeben der Bewertung: " + e.message);
    }
});

// ===================== MEINE BEWERTUNGEN =====================
// ===================== MEINE BEWERTUNGEN =====================
async function loadMyRatings() {
    const container = document.getElementById("myRatingsList");
    if (!currentUser) {
        container.innerHTML = "<p>Bitte melden Sie sich an, um Ihre Bewertungen zu sehen.</p>";
        return;
    }
    try {
        const ratings = await apiFetch("/api/ratings/me");
        if (ratings.length === 0) {
            container.innerHTML = "<p>Sie haben noch keine Bewertungen abgegeben.</p>";
            return;
        }
        let html = "";
        ratings.forEach(r => {
            const stars = "★".repeat(r.grade) + "☆".repeat(5 - r.grade);
            html += `
                <div class="rating-item w3-card w3-padding w3-margin-bottom" data-rating-id="${r.id}">
                    <b>${r.poiName || "Unbekannte Kneipe"}</b>
                    <span class="rating-stars">${stars}</span>
                    <span class="rating-date">${r.createdAt ? new Date(r.createdAt).toLocaleDateString() : ""}</span>
                    <p id="rating-text-${r.id}">${r.txt || ""}</p>
                    <div>
                        <button class="w3-button w3-blue w3-small edit-rating-btn" data-id="${r.id}">Bearbeiten</button>
                        <button class="w3-button w3-red w3-small delete-rating-btn" data-id="${r.id}">Löschen</button>
                    </div>
                </div>
            `;
        });
        container.innerHTML = html;

        // Attacher les événements après le rendu
        document.querySelectorAll('.delete-rating-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const id = parseInt(this.dataset.id);
                deleteRating(id);
            });
        });
        document.querySelectorAll('.edit-rating-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const id = parseInt(this.dataset.id);
                openEditForm(id);
            });
        });
    } catch (e) {
        console.error(e);
        container.innerHTML = "<p>Fehler beim Laden Ihrer Bewertungen.</p>";
    }
}

// ===================== REGISTRIERUNG =====================
document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const data = {
        username: document.getElementById("regUsername").value,
        email: document.getElementById("regEmail").value,
        firstname: document.getElementById("regFirstname").value,
        lastname: document.getElementById("regLastname").value,
        street: document.getElementById("regStreet").value,
        streetNr: document.getElementById("regStreetNr").value,
        zip: document.getElementById("regZip").value,
        city: document.getElementById("regCity").value,
        password: document.getElementById("regPassword").value
    };
    try {
        await apiFetch("/api/register", { method: "POST", body: JSON.stringify(data) });
        alert("Registrierung erfolgreich! Bitte melden Sie sich an.");
        document.getElementById('registerModal').style.display = 'none';
        document.getElementById('loginModal').style.display = 'block';
    } catch (e) {
        alert("Fehler bei der Registrierung: " + e.message);
    }
});

// ===================== LOGIN =====================
document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const data = {
        username: document.getElementById("loginUsername").value,
        password: document.getElementById("loginPassword").value
    };
    try {
        const user = await apiFetch("/api/login", { method: "POST", body: JSON.stringify(data) });
        currentUser = user;
        updateUI();
        document.getElementById('loginModal').style.display = 'none';
        alert("Erfolgreich angemeldet!");
        await loadMyRatings();
    } catch (e) {
        alert("Anmeldung fehlgeschlagen: " + e.message);
    }
});

// ===================== LOGOUT =====================
document.getElementById("logoutBtn").addEventListener("click", async () => {
    try {
        const message = await apiFetch("/api/logout", { method: "POST" });
        alert(message); // "Logged out successfully"
        currentUser = null;
        updateUI();
        document.getElementById("ratingsList").innerHTML = "<p>Keine Bewertungen vorhanden.</p>";
        document.getElementById("myRatingsList").innerHTML = "<p>Sie haben noch keine Bewertungen abgegeben.</p>";
    } catch (e) {
        alert("Fehler beim Abmelden: " + e.message);
    }
});

// ===================== OUVERTURE MODALES =====================
document.getElementById("loginOpenBtn").addEventListener("click", () => {
    document.getElementById('loginModal').style.display = 'block';
});
document.getElementById("registerOpenBtn").addEventListener("click", () => {
    document.getElementById('registerModal').style.display = 'block';
});

// ===================== INIT =====================
function init() {
    initStars();
    loadPois();
    updateUI();
    // Vérifier session utilisateur
    apiFetch("/api/users/me").then(user => {
        if (user && user.id) {
            currentUser = user;
            updateUI();
            loadMyRatings();
        }
    }).catch(() => {});
}
// ===================== SUPPRESSION DU COMPTE (BONUS 4) =====================
// ===================== SUPPRESSION DU COMPTE =====================
document.getElementById("deleteAccountBtn").addEventListener("click", async () => {
    if (confirm("Sind Sie sicher, dass Sie Ihr Konto löschen möchten? Alle Ihre Bewertungen werden ebenfalls gelöscht!")) {
        try {
            const message = await apiFetch("/api/users/me", { method: "DELETE" });
            alert(message); // "Konto erfolgreich gelöscht"
            currentUser = null;
            updateUI();
            document.getElementById("ratingsList").innerHTML = "<p>Keine Bewertungen vorhanden.</p>";
            document.getElementById("myRatingsList").innerHTML = "<p>Sie haben noch keine Bewertungen abgegeben.</p>";
            // Réinitialiser les infos
            document.getElementById("poiNameDisplay").textContent = "Wähle eine Kneipe auf der Karte";
            document.getElementById("poiAddress").textContent = "-";
            document.getElementById("poiPhone").textContent = "-";
            document.getElementById("poiCuisine").textContent = "-";
        } catch (e) {
            alert("Fehler beim Löschen: " + e.message);
        }
    }
});
// ===================== SUPPRESSION D'UN AVIS (Bonus 5) =====================
async function deleteRating(ratingId) {
    if (!confirm("Bewertung wirklich löschen?")) return;
    try {
        const message = await apiFetch(`/api/ratings/${ratingId}`, { method: "DELETE" });
        alert(message); // "Bewertung erfolgreich gelöscht"
        // Rafraîchir la liste
        await loadMyRatings();
        // Si le POI actuellement affiché correspond, rafraîchir aussi les avis
        if (currentPoiId) {
            await loadRatings(currentPoiId);
        }
    } catch (e) {
        alert("Fehler beim Löschen der Bewertung: " + e.message);
    }
}

// ===================== OUVRIRE LE FORMULAIRE DE MODIFICATION =====================
function openEditForm(ratingId) {
    // Trouver l'élément correspondant
    const item = document.querySelector(`.rating-item[data-rating-id="${ratingId}"]`);
    if (!item) return;
    const textEl = document.getElementById(`rating-text-${ratingId}`);
    const currentText = textEl ? textEl.textContent : "";
    // Récupérer la note actuelle (via l'affichage étoiles)
    const starsSpan = item.querySelector('.rating-stars');
    const currentGrade = starsSpan ? starsSpan.textContent.split('★').length - 1 : 0;

    document.getElementById("editRatingId").value = ratingId;
    document.getElementById("editRatingComment").value = currentText;
    document.getElementById("editRatingGrade").value = currentGrade;

    // Mettre à jour les étoiles de l'édition
    const stars = document.querySelectorAll('.edit-star-icon');
    stars.forEach(s => {
        const val = parseInt(s.dataset.value);
        s.classList.toggle('active', val <= currentGrade);
    });

    document.getElementById("editRatingForm").style.display = "block";
    window.scrollTo({ top: document.getElementById("editRatingForm").offsetTop, behavior: 'smooth' });
}

// ===================== MODIFICATION D'UN AVIS (Bonus 5) =====================
document.getElementById("editRatingFormInner").addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = parseInt(document.getElementById("editRatingId").value);
    const grade = parseInt(document.getElementById("editRatingGrade").value);
    const txt = document.getElementById("editRatingComment").value.trim();
    if (grade === 0) {
        alert("Bitte wählen Sie eine Bewertung (1-5 Sterne).");
        return;
    }
    if (!txt) {
        alert("Bitte schreiben Sie einen Kommentar.");
        return;
    }
    try {
        const message = await apiFetch(`/api/ratings/${id}`, {
            method: "PUT",
            body: JSON.stringify({ grade, txt })
        });
        alert(message);
        document.getElementById("editRatingForm").style.display = "none";
        // Rafraîchir les listes
        await loadMyRatings();
        if (currentPoiId) {
            await loadRatings(currentPoiId);
        }
    } catch (e) {
        alert("Fehler beim Aktualisieren: " + e.message);
    }
});

// ===================== ANNULER L'ÉDITION =====================
document.getElementById("cancelEditBtn").addEventListener("click", () => {
    document.getElementById("editRatingForm").style.display = "none";
});

// ===================== GESTION DES ÉTOILES DANS LE FORMULAIRE D'ÉDITION =====================
document.querySelectorAll('.edit-star-icon').forEach(el => {
    el.addEventListener('click', function() {
        const val = parseInt(this.dataset.value);
        document.getElementById("editRatingGrade").value = val;
        document.querySelectorAll('.edit-star-icon').forEach(s => {
            s.classList.toggle('active', parseInt(s.dataset.value) <= val);
        });
    });
});
document.addEventListener('DOMContentLoaded', init);