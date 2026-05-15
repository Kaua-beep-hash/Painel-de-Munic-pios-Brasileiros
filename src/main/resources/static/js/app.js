/* ==========================================
   Municípios BR — Lógica Frontend
   ========================================== */

(function () {
    'use strict';

    const selectEstado   = document.getElementById('selectEstado');
    const inputBusca     = document.getElementById('inputBusca');
    const groupBusca     = document.getElementById('groupBusca');
    const loader         = document.getElementById('loader');
    const erroInline     = document.getElementById('erroInline');
    const erroMensagem   = document.getElementById('erroMensagem');
    const gridMunicipios = document.getElementById('gridMunicipios');
    const estadoVazio    = document.getElementById('estadoVazio');
    const statsBar       = document.getElementById('statsBar');
    const totalMunicipios = document.getElementById('totalMunicipios');
    const totalFiltrados  = document.getElementById('totalFiltrados');
    const estadoNome      = document.getElementById('estadoNome');

    let todosMunicipios = [];
    let debounceTimer   = null;

    /* ---- Helpers ---- */
    function mostrar(el) { el.style.display = ''; }
    function ocultar(el) { el.style.display = 'none'; }

    function setEstado(uf, sigla) {
        estadoNome.textContent = sigla || '—';
    }

    function renderCards(lista) {
        gridMunicipios.innerHTML = '';

        lista.forEach((m, i) => {
            const card = document.createElement('div');
            card.className = 'card-municipio';
            card.style.animationDelay = `${Math.min(i * 0.02, 0.4)}s`;

            const micro = m.microrregiao?.nome || '—';
            const meso  = m.microrregiao?.mesorregiao?.nome || '';

            card.innerHTML = `
                <div class="card-codigo">IBGE ${m.id}</div>
                <div class="card-nome">${m.nome}</div>
                <div class="card-micro" title="${micro}${meso ? ' · ' + meso : ''}">
                    ${micro}${meso ? ' · ' + meso : ''}
                </div>
            `;
            gridMunicipios.appendChild(card);
        });
    }

    function aplicarFiltro() {
        const termo = inputBusca.value.trim().toLowerCase();
        const filtrados = termo
            ? todosMunicipios.filter(m => m.nome.toLowerCase().includes(termo))
            : todosMunicipios;

        renderCards(filtrados);
        totalFiltrados.textContent = filtrados.length;

        if (filtrados.length === 0 && termo) {
            gridMunicipios.innerHTML = `
                <div style="grid-column:1/-1;text-align:center;padding:2rem;color:var(--cinza);">
                    Nenhum município encontrado para "<strong>${termo}</strong>"
                </div>`;
        }
    }

    function mostrarErro(msg) {
        erroMensagem.textContent = msg;
        mostrar(erroInline);
    }

    /* ---- Carrega municípios ---- */
    async function carregarMunicipios(uf, nomeEstado) {
        // Reset
        ocultar(erroInline);
        ocultar(statsBar);
        ocultar(estadoVazio);
        ocultar(groupBusca);
        gridMunicipios.innerHTML = '';
        inputBusca.value = '';
        todosMunicipios = [];
        mostrar(loader);

        try {
            const resp = await fetch(`/api/municipios/${uf}`);
            const data = await resp.json();

            if (!resp.ok) {
                throw new Error(data.erro || 'Erro desconhecido ao consultar a API.');
            }

            todosMunicipios = data;

            totalMunicipios.textContent = data.length;
            totalFiltrados.textContent  = data.length;
            estadoNome.textContent      = nomeEstado;

            mostrar(statsBar);
            mostrar(groupBusca);
            renderCards(data);

        } catch (err) {
            mostrarErro(err.message || 'Não foi possível carregar os municípios. Tente novamente.');
            mostrar(estadoVazio);
        } finally {
            ocultar(loader);
        }
    }

    /* ---- Eventos ---- */
    selectEstado.addEventListener('change', function () {
        const uf = this.value;
        if (!uf) {
            ocultar(statsBar);
            ocultar(groupBusca);
            ocultar(erroInline);
            gridMunicipios.innerHTML = '';
            todosMunicipios = [];
            mostrar(estadoVazio);
            return;
        }
        const nomeEstado = this.options[this.selectedIndex].text
            .replace(/\s*\(.*\)$/, ''); // Remove "(XX)" do label
        carregarMunicipios(uf, nomeEstado);
    });

    inputBusca.addEventListener('input', function () {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(aplicarFiltro, 250);
    });

    /* ---- Estado inicial ---- */
    mostrar(estadoVazio);

})();
