/* ==========================================================================
   KotlinFlow - Modern Documentation & Showcase JavaScript
   Interactive Canvas, Theme Toggle, Code Tabs, Search Modal & Micro-Interactions
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {

  // ==========================================================================
  // 1. Theme Management (Dark / Light)
  // ==========================================================================
  const themeToggle = document.getElementById('theme-toggle');
  const sunIcon = themeToggle?.querySelector('.icon-sun');
  const moonIcon = themeToggle?.querySelector('.icon-moon');
  const html = document.documentElement;

  function applyTheme(theme) {
    html.setAttribute('data-theme', theme);
    localStorage.setItem('vitepress-theme-appearance', theme);
    if (theme === 'light') {
      if (sunIcon) sunIcon.style.display = 'none';
      if (moonIcon) moonIcon.style.display = 'block';
    } else {
      if (sunIcon) sunIcon.style.display = 'block';
      if (moonIcon) moonIcon.style.display = 'none';
    }
    // Re-draw canvas with updated theme colors
    drawCanvas();
  }

  // Initial Theme Check
  const savedTheme = localStorage.getItem('vitepress-theme-appearance');
  if (savedTheme) {
    applyTheme(savedTheme);
  } else if (window.matchMedia && window.matchMedia('(prefers-color-scheme: light)').matches) {
    applyTheme('light');
  } else {
    applyTheme('dark');
  }

  themeToggle?.addEventListener('click', () => {
    const currentTheme = html.getAttribute('data-theme') || 'dark';
    applyTheme(currentTheme === 'dark' ? 'light' : 'dark');
  });

  // ==========================================================================
  // 2. Visual Showcase Device Tabs
  // ==========================================================================
  const showcaseTabs = document.querySelectorAll('.showcase-tab');
  const showcaseImg = document.getElementById('showcase-img');
  const showcaseCaption = document.getElementById('showcase-caption');

  showcaseTabs.forEach(tab => {
    tab.addEventListener('click', () => {
      showcaseTabs.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');

      const imgPath = tab.getAttribute('data-img');
      const captionText = tab.getAttribute('data-caption');

      if (showcaseImg && imgPath) {
        showcaseImg.style.opacity = '0.3';
        showcaseImg.style.transform = 'scale(0.98)';
        setTimeout(() => {
          showcaseImg.src = imgPath;
          showcaseImg.style.opacity = '1';
          showcaseImg.style.transform = 'scale(1)';
        }, 150);
      }

      if (showcaseCaption && captionText) {
        showcaseCaption.textContent = captionText;
      }
    });
  });

  // ==========================================================================
  // 3. Code Quickstart Tabs & Clipboard Copy
  // ==========================================================================
  const codeTabs = document.querySelectorAll('.code-tab');
  const codePanes = document.querySelectorAll('.code-pane');
  const btnCopy = document.getElementById('btn-copy-code');
  const toast = document.getElementById('toast');

  codeTabs.forEach(tab => {
    tab.addEventListener('click', () => {
      codeTabs.forEach(t => t.classList.remove('active'));
      codePanes.forEach(p => p.classList.remove('active'));

      tab.classList.add('active');
      const targetId = tab.getAttribute('data-target');
      const targetPane = document.getElementById(targetId);
      if (targetPane) targetPane.classList.add('active');
    });
  });

  function showToast(message) {
    if (!toast) return;
    toast.textContent = message;
    toast.classList.add('show');
    setTimeout(() => {
      toast.classList.remove('show');
    }, 2400);
  }

  btnCopy?.addEventListener('click', () => {
    const activePane = document.querySelector('.code-pane.active pre code');
    if (activePane) {
      navigator.clipboard.writeText(activePane.innerText).then(() => {
        showToast('✓ Code copied to clipboard!');
      }).catch(() => {
        showToast('Failed to copy');
      });
    }
  });

  // ==========================================================================
  // 4. Search Modal (Cmd+K / Ctrl+K)
  // ==========================================================================
  const searchModal = document.getElementById('search-modal');
  const searchTrigger = document.getElementById('search-trigger');
  const searchClose = document.getElementById('search-close');
  const searchInput = document.getElementById('search-input');
  const searchResults = document.getElementById('search-results');
  const searchItems = document.querySelectorAll('.search-item');

  function openSearch() {
    searchModal?.classList.add('open');
    searchInput?.focus();
    if (searchInput) searchInput.value = '';
    searchItems.forEach(item => item.style.display = 'block');
  }

  function closeSearch() {
    searchModal?.classList.remove('open');
  }

  searchTrigger?.addEventListener('click', openSearch);
  searchClose?.addEventListener('click', closeSearch);

  searchModal?.addEventListener('click', (e) => {
    if (e.target === searchModal) closeSearch();
  });

  document.addEventListener('keydown', (e) => {
    if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
      e.preventDefault();
      openSearch();
    }
    if (e.key === 'Escape' && searchModal?.classList.contains('open')) {
      closeSearch();
    }
  });

  searchInput?.addEventListener('input', (e) => {
    const query = e.target.value.toLowerCase().trim();
    searchItems.forEach(item => {
      const text = item.textContent.toLowerCase();
      item.style.display = text.includes(query) ? 'block' : 'none';
    });
  });

  searchItems.forEach(item => {
    item.addEventListener('click', () => {
      const href = item.getAttribute('data-href');
      if (href) {
        closeSearch();
        window.location.hash = href;
      }
    });
  });

  // ==========================================================================
  // 5. Interactive Live Hero Canvas (Draggable Nodes & Pulse Curves)
  // ==========================================================================
  const canvas = document.getElementById('interactive-canvas');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  const btnResetCanvas = document.getElementById('btn-reset-canvas');

  let width = 0;
  let height = 0;
  let dpr = window.devicePixelRatio || 1;

  // Initial Node Topology
  const initialNodes = [
    { id: '1', title: 'User Request', badge: 'Trigger', color: '#10B981', x: 80, y: 180, w: 170, h: 72 },
    { id: '2', title: 'Agent Router', badge: 'LLM Node', color: '#7F52FF', x: 330, y: 150, w: 180, h: 84 },
    { id: '3', title: 'Tool: Calendar', badge: 'Action', color: '#06B6D4', x: 610, y: 70, w: 170, h: 72 },
    { id: '4', title: 'Human Approval', badge: 'Review', color: '#F59E0B', x: 610, y: 250, w: 180, h: 72 },
    { id: '5', title: 'Final Dispatch', badge: 'Output', color: '#EC4899', x: 890, y: 180, w: 170, h: 72 }
  ];

  let nodes = JSON.parse(JSON.stringify(initialNodes));

  const edges = [
    { from: '1', to: '2', speed: 0.008, particles: [0.1, 0.5, 0.9] },
    { from: '2', to: '3', speed: 0.007, particles: [0.2, 0.7] },
    { from: '2', to: '4', speed: 0.009, particles: [0.3, 0.8] },
    { from: '3', to: '5', speed: 0.007, particles: [0.4, 0.95] },
    { from: '4', to: '5', speed: 0.008, particles: [0.15, 0.65] }
  ];

  function resizeCanvas() {
    const rect = canvas.getBoundingClientRect();
    width = rect.width;
    height = rect.height;
    dpr = window.devicePixelRatio || 1;

    canvas.width = width * dpr;
    canvas.height = height * dpr;
    ctx.scale(dpr, dpr);

    // Responsive scaling of initial positions if canvas is narrower
    if (width < 900) {
      const scaleFactor = width / 1000;
      nodes.forEach((n, i) => {
        n.x = initialNodes[i].x * scaleFactor + 10;
        n.y = initialNodes[i].y * scaleFactor + 30;
      });
    }
  }

  window.addEventListener('resize', resizeCanvas);
  resizeCanvas();

  btnResetCanvas?.addEventListener('click', () => {
    nodes = JSON.parse(JSON.stringify(initialNodes));
    resizeCanvas();
  });

  // Interaction State
  let draggedNode = null;
  let dragOffsetX = 0;
  let dragOffsetY = 0;

  function getMousePos(e) {
    const rect = canvas.getBoundingClientRect();
    const clientX = e.touches ? e.touches[0].clientX : e.clientX;
    const clientY = e.touches ? e.touches[0].clientY : e.clientY;
    return {
      x: clientX - rect.left,
      y: clientY - rect.top
    };
  }

  function findNodeUnder(x, y) {
    for (let i = nodes.length - 1; i >= 0; i--) {
      const n = nodes[i];
      if (x >= n.x && x <= n.x + n.w && y >= n.y && y <= n.y + n.h) {
        return n;
      }
    }
    return null;
  }

  canvas.addEventListener('mousedown', (e) => {
    const pos = getMousePos(e);
    const node = findNodeUnder(pos.x, pos.y);
    if (node) {
      draggedNode = node;
      dragOffsetX = pos.x - node.x;
      dragOffsetY = pos.y - node.y;
    }
  });

  window.addEventListener('mousemove', (e) => {
    if (!draggedNode) return;
    const pos = getMousePos(e);
    draggedNode.x = Math.max(10, Math.min(width - draggedNode.w - 10, pos.x - dragOffsetX));
    draggedNode.y = Math.max(10, Math.min(height - draggedNode.h - 10, pos.y - dragOffsetY));
  });

  window.addEventListener('mouseup', () => {
    draggedNode = null;
  });

  // Touch Support
  canvas.addEventListener('touchstart', (e) => {
    const pos = getMousePos(e);
    const node = findNodeUnder(pos.x, pos.y);
    if (node) {
      draggedNode = node;
      dragOffsetX = pos.x - node.x;
      dragOffsetY = pos.y - node.y;
      e.preventDefault();
    }
  }, { passive: false });

  window.addEventListener('touchmove', (e) => {
    if (!draggedNode) return;
    const pos = getMousePos(e);
    draggedNode.x = Math.max(10, Math.min(width - draggedNode.w - 10, pos.x - dragOffsetX));
    draggedNode.y = Math.max(10, Math.min(height - draggedNode.h - 10, pos.y - dragOffsetY));
  }, { passive: false });

  window.addEventListener('touchend', () => {
    draggedNode = null;
  });

  // Render Loop
  function drawCanvas() {
    ctx.clearRect(0, 0, width, height);

    const isLight = html.getAttribute('data-theme') === 'light';
    const cardBg = isLight ? '#FFFFFF' : '#141A28';
    const cardBorder = isLight ? 'rgba(0, 0, 0, 0.12)' : 'rgba(255, 255, 255, 0.14)';
    const textColor = isLight ? '#0F172A' : '#F8FAFC';
    const textMuted = isLight ? '#64748B' : '#94A3B8';
    const edgeColor = isLight ? 'rgba(100, 116, 139, 0.45)' : 'rgba(255, 255, 255, 0.25)';

    // 1. Draw Edges
    edges.forEach(edge => {
      const fromNode = nodes.find(n => n.id === edge.from);
      const toNode = nodes.find(n => n.id === edge.to);
      if (!fromNode || !toNode) return;

      const startX = fromNode.x + fromNode.w;
      const startY = fromNode.y + fromNode.h / 2;
      const endX = toNode.x;
      const endY = toNode.y + toNode.h / 2;
      const dx = Math.max(40, (endX - startX) * 0.55);

      // Bezier Path
      ctx.beginPath();
      ctx.moveTo(startX, startY);
      ctx.bezierCurveTo(startX + dx, startY, endX - dx, endY, endX, endY);
      ctx.strokeStyle = edgeColor;
      ctx.lineWidth = 2.5;
      ctx.stroke();

      // Flowing Pulse Particles
      edge.particles.forEach((p, index) => {
        edge.particles[index] = (p + edge.speed) % 1;
        const t = edge.particles[index];

        // Cubic Bezier interpolation
        const u = 1 - t;
        const px = u * u * u * startX + 3 * u * u * t * (startX + dx) + 3 * u * t * t * (endX - dx) + t * t * t * endX;
        const py = u * u * u * startY + 3 * u * u * t * startY + 3 * u * t * t * endY + t * t * t * endY;

        ctx.beginPath();
        ctx.arc(px, py, 4, 0, Math.PI * 2);
        ctx.fillStyle = fromNode.color;
        ctx.shadowColor = fromNode.color;
        ctx.shadowBlur = 10;
        ctx.fill();
        ctx.shadowBlur = 0; // reset
      });
    });

    // 2. Draw Nodes
    nodes.forEach(node => {
      const radius = 12;

      // Card Shadow
      ctx.shadowColor = isLight ? 'rgba(0,0,0,0.08)' : 'rgba(0,0,0,0.5)';
      ctx.shadowBlur = 16;
      ctx.shadowOffsetY = 6;

      // Card Container
      ctx.beginPath();
      ctx.roundRect(node.x, node.y, node.w, node.h, radius);
      ctx.fillStyle = cardBg;
      ctx.fill();

      // Card Border
      ctx.shadowBlur = 0;
      ctx.shadowOffsetY = 0;
      ctx.strokeStyle = cardBorder;
      ctx.lineWidth = 1.2;
      ctx.stroke();

      // Left Accent Strip
      ctx.beginPath();
      ctx.roundRect(node.x, node.y, 4, node.h, [radius, 0, 0, radius]);
      ctx.fillStyle = node.color;
      ctx.fill();

      // Node Badge
      ctx.font = '600 10px JetBrains Mono, monospace';
      ctx.fillStyle = node.color;
      ctx.fillText(node.badge.toUpperCase(), node.x + 14, node.y + 24);

      // Node Title
      ctx.font = '600 13px Inter, sans-serif';
      ctx.fillStyle = textColor;
      ctx.fillText(node.title, node.x + 14, node.y + 44);

      // Connection Handles (Circle Pins)
      // Input Handle (Left)
      if (node.id !== '1') {
        ctx.beginPath();
        ctx.arc(node.x, node.y + node.h / 2, 5, 0, Math.PI * 2);
        ctx.fillStyle = node.color;
        ctx.strokeStyle = cardBg;
        ctx.lineWidth = 2;
        ctx.fill();
        ctx.stroke();
      }

      // Output Handle (Right)
      if (node.id !== '5') {
        ctx.beginPath();
        ctx.arc(node.x + node.w, node.y + node.h / 2, 5, 0, Math.PI * 2);
        ctx.fillStyle = node.color;
        ctx.strokeStyle = cardBg;
        ctx.lineWidth = 2;
        ctx.fill();
        ctx.stroke();
      }
    });

    requestAnimationFrame(drawCanvas);
  }

  drawCanvas();

});
