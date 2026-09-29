/* ==========================================================================
   KotlinFlow - Modern Documentation & Showcase JavaScript
   Rock-Solid Interactive Canvas, Theme Toggle, Code Tabs, Search & Mobile Nav
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

  themeToggle?.addEventListener('click', (e) => {
    e.preventDefault();
    const currentTheme = html.getAttribute('data-theme') || 'dark';
    applyTheme(currentTheme === 'dark' ? 'light' : 'dark');
  });

  // ==========================================================================
  // 2. Mobile Navigation Drawer
  // ==========================================================================
  const mobileMenuToggle = document.getElementById('mobile-menu-toggle');
  const mobileNavDrawer = document.getElementById('mobile-nav-drawer');

  mobileMenuToggle?.addEventListener('click', (e) => {
    e.preventDefault();
    mobileNavDrawer?.classList.toggle('open');
  });

  document.querySelectorAll('.mobile-nav-link').forEach(link => {
    link.addEventListener('click', () => {
      mobileNavDrawer?.classList.remove('open');
    });
  });

  // ==========================================================================
  // 3. Smooth Anchor Scrolling (Offset for Sticky Navbar)
  // ==========================================================================
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
      const targetId = this.getAttribute('href');
      if (!targetId || targetId === '#') return;
      const targetEl = document.querySelector(targetId);
      if (targetEl) {
        e.preventDefault();
        targetEl.scrollIntoView({ behavior: 'smooth' });
        history.pushState(null, null, targetId);
      }
    });
  });

  // ==========================================================================
  // 4. Visual Showcase Device Tabs
  // ==========================================================================
  const showcaseTabs = document.querySelectorAll('.showcase-tab');
  const showcaseImg = document.getElementById('showcase-img');
  const showcaseCaption = document.getElementById('showcase-caption');

  // Preload all showcase images for instant switching
  showcaseTabs.forEach(tab => {
    const imgPath = tab.getAttribute('data-img');
    if (imgPath) {
      const preloadImg = new Image();
      preloadImg.src = imgPath;
    }
  });

  showcaseTabs.forEach(tab => {
    tab.addEventListener('click', (e) => {
      e.preventDefault();
      showcaseTabs.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');

      const imgPath = tab.getAttribute('data-img');
      const captionText = tab.getAttribute('data-caption');

      if (showcaseImg && imgPath) {
        showcaseImg.style.opacity = '0.2';
        showcaseImg.style.transform = 'scale(0.97)';
        setTimeout(() => {
          showcaseImg.src = imgPath;
          showcaseImg.style.opacity = '1';
          showcaseImg.style.transform = 'scale(1)';
        }, 120);
      }

      if (showcaseCaption && captionText) {
        showcaseCaption.textContent = captionText;
      }
    });
  });

  // ==========================================================================
  // 5. Code Quickstart Tabs & Robust Clipboard Copy
  // ==========================================================================
  const codeTabs = document.querySelectorAll('.code-tab');
  const codePanes = document.querySelectorAll('.code-pane');
  const btnCopy = document.getElementById('btn-copy-code');
  const toast = document.getElementById('toast');

  codeTabs.forEach(tab => {
    tab.addEventListener('click', (e) => {
      e.preventDefault();
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

  function copyTextToClipboard(text) {
    if (navigator.clipboard && window.isSecureContext) {
      navigator.clipboard.writeText(text).then(() => {
        showToast('✓ Code copied to clipboard!');
      }).catch(() => {
        fallbackCopy(text);
      });
    } else {
      fallbackCopy(text);
    }
  }

  function fallbackCopy(text) {
    const ta = document.createElement('textarea');
    ta.value = text;
    ta.style.position = 'fixed';
    ta.style.left = '-9999px';
    ta.style.top = '-9999px';
    ta.style.opacity = '0';
    document.body.appendChild(ta);
    ta.focus();
    ta.select();
    try {
      document.execCommand('copy');
      showToast('✓ Code copied to clipboard!');
    } catch (err) {
      showToast('Press Cmd+C to copy');
    }
    document.body.removeChild(ta);
  }

  btnCopy?.addEventListener('click', (e) => {
    e.preventDefault();
    const activePane = document.querySelector('.code-pane.active pre code');
    if (activePane) {
      copyTextToClipboard(activePane.innerText);
    }
  });

  // ==========================================================================
  // 6. Search Modal (Cmd+K / Ctrl+K)
  // ==========================================================================
  const searchModal = document.getElementById('search-modal');
  const searchTrigger = document.getElementById('search-trigger');
  const searchClose = document.getElementById('search-close');
  const searchInput = document.getElementById('search-input');
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

  searchTrigger?.addEventListener('click', (e) => {
    e.preventDefault();
    openSearch();
  });

  searchClose?.addEventListener('click', (e) => {
    e.preventDefault();
    closeSearch();
  });

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
        const targetEl = document.querySelector(href);
        if (targetEl) {
          targetEl.scrollIntoView({ behavior: 'smooth' });
          history.pushState(null, null, href);
        }
      }
    });
  });

  // ==========================================================================
  // 7. Interactive Live Hero Canvas (Draggable Nodes & Pulse Curves)
  // ==========================================================================
  const canvas = document.getElementById('interactive-canvas');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  const btnResetCanvas = document.getElementById('btn-reset-canvas');

  let width = 0;
  let height = 0;
  let dpr = window.devicePixelRatio || 1;

  // Universal Cross-Browser Rounded Rectangle (100% Safe)
  function drawRoundedRect(c, x, y, w, h, r) {
    r = Math.min(r, w / 2, h / 2);
    c.beginPath();
    c.moveTo(x + r, y);
    c.lineTo(x + w - r, y);
    c.arcTo(x + w, y, x + w, y + r, r);
    c.lineTo(x + w, y + h - r);
    c.arcTo(x + w, y + h, x + w - r, y + h, r);
    c.lineTo(x + r, y + h);
    c.arcTo(x, y + h, x, y + h - r, r);
    c.lineTo(x, y + r);
    c.arcTo(x, y, x + r, y, r);
    c.closePath();
  }

  // Responsive Node Positions Generator
  function calculateNodes(w, h) {
    const isMobile = w < 680;
    const nodeW = isMobile ? 120 : 160;
    const nodeH = isMobile ? 56 : 68;

    if (isMobile) {
      // Clean mobile arrangement
      return [
        { id: '1', title: 'User Input', badge: 'Trigger', color: '#10B981', x: 20, y: 30, w: nodeW, h: nodeH },
        { id: '2', title: 'AI Agent', badge: 'LLM Node', color: '#7F52FF', x: (w - nodeW) / 2, y: 130, w: nodeW, h: nodeH },
        { id: '3', title: 'Calendar', badge: 'Tool', color: '#06B6D4', x: 16, y: 240, w: nodeW, h: nodeH },
        { id: '4', title: 'Approval', badge: 'Human', color: '#F59E0B', x: w - nodeW - 16, y: 240, w: nodeW, h: nodeH },
        { id: '5', title: 'Dispatch', badge: 'Output', color: '#EC4899', x: (w - nodeW) / 2, y: 340, w: nodeW, h: nodeH }
      ];
    } else {
      // Spacious desktop pipeline
      return [
        { id: '1', title: 'User Request', badge: 'Trigger', color: '#10B981', x: Math.max(30, w * 0.05), y: h * 0.42, w: nodeW, h: nodeH },
        { id: '2', title: 'Agent Router', badge: 'LLM Node', color: '#7F52FF', x: w * 0.28, y: h * 0.38, w: nodeW + 12, h: nodeH + 6 },
        { id: '3', title: 'Tool: Calendar', badge: 'Action', color: '#06B6D4', x: w * 0.54, y: h * 0.16, w: nodeW, h: nodeH },
        { id: '4', title: 'Human Approval', badge: 'Review', color: '#F59E0B', x: w * 0.54, y: h * 0.62, w: nodeW, h: nodeH },
        { id: '5', title: 'Final Dispatch', badge: 'Output', color: '#EC4899', x: Math.min(w - nodeW - 30, w * 0.78), y: h * 0.42, w: nodeW, h: nodeH }
      ];
    }
  }

  let nodes = [];

  const edges = [
    { from: '1', to: '2', speed: 0.008, particles: [0.1, 0.55] },
    { from: '2', to: '3', speed: 0.007, particles: [0.2, 0.75] },
    { from: '2', to: '4', speed: 0.009, particles: [0.35, 0.85] },
    { from: '3', to: '5', speed: 0.007, particles: [0.4, 0.95] },
    { from: '4', to: '5', speed: 0.008, particles: [0.15, 0.65] }
  ];

  function resizeCanvas() {
    const rect = canvas.getBoundingClientRect();
    width = rect.width;
    height = rect.height;
    dpr = window.devicePixelRatio || 1;

    canvas.width = Math.round(width * dpr);
    canvas.height = Math.round(height * dpr);

    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.scale(dpr, dpr);

    nodes = calculateNodes(width, height);
  }

  window.addEventListener('resize', resizeCanvas);
  resizeCanvas();

  btnResetCanvas?.addEventListener('click', (e) => {
    e.preventDefault();
    nodes = calculateNodes(width, height);
    showToast('Canvas reset to initial layout');
  });

  // Pointer Interaction
  let draggedNode = null;
  let dragOffsetX = 0;
  let dragOffsetY = 0;

  function getCanvasCoords(e) {
    const rect = canvas.getBoundingClientRect();
    return {
      x: e.clientX - rect.left,
      y: e.clientY - rect.top
    };
  }

  function findNodeAt(x, y) {
    for (let i = nodes.length - 1; i >= 0; i--) {
      const n = nodes[i];
      if (x >= n.x && x <= n.x + n.w && y >= n.y && y <= n.y + n.h) {
        return n;
      }
    }
    return null;
  }

  canvas.addEventListener('pointerdown', (e) => {
    const pos = getCanvasCoords(e);
    const node = findNodeAt(pos.x, pos.y);
    if (node) {
      draggedNode = node;
      dragOffsetX = pos.x - node.x;
      dragOffsetY = pos.y - node.y;
      try {
        canvas.setPointerCapture(e.pointerId);
      } catch (err) {}
      canvas.style.cursor = 'grabbing';
      e.preventDefault();
    }
  });

  canvas.addEventListener('pointermove', (e) => {
    const pos = getCanvasCoords(e);
    if (draggedNode) {
      draggedNode.x = Math.max(6, Math.min(width - draggedNode.w - 6, pos.x - dragOffsetX));
      draggedNode.y = Math.max(6, Math.min(height - draggedNode.h - 6, pos.y - dragOffsetY));
    } else {
      const node = findNodeAt(pos.x, pos.y);
      canvas.style.cursor = node ? 'grab' : 'default';
    }
  });

  function stopDrag(e) {
    if (draggedNode) {
      try {
        canvas.releasePointerCapture(e.pointerId);
      } catch (err) {}
      draggedNode = null;
      canvas.style.cursor = 'default';
    }
  }

  canvas.addEventListener('pointerup', stopDrag);
  canvas.addEventListener('pointercancel', stopDrag);

  // Render Loop
  function drawCanvas() {
    ctx.clearRect(0, 0, width, height);

    const isLight = html.getAttribute('data-theme') === 'light';
    const cardBg = isLight ? '#FFFFFF' : '#141A28';
    const cardBorder = isLight ? 'rgba(0, 0, 0, 0.12)' : 'rgba(255, 255, 255, 0.16)';
    const textColor = isLight ? '#0F172A' : '#F8FAFC';
    const edgeColor = isLight ? 'rgba(100, 116, 139, 0.45)' : 'rgba(255, 255, 255, 0.28)';

    // 1. Draw Edges
    edges.forEach(edge => {
      const fromNode = nodes.find(n => n.id === edge.from);
      const toNode = nodes.find(n => n.id === edge.to);
      if (!fromNode || !toNode) return;

      const isVertical = Math.abs(toNode.y - fromNode.y) > Math.abs(toNode.x - fromNode.x) && (width < 680);

      let startX, startY, endX, endY, cp1x, cp1y, cp2x, cp2y;

      if (isVertical) {
        startX = fromNode.x + fromNode.w / 2;
        startY = fromNode.y + fromNode.h;
        endX = toNode.x + toNode.w / 2;
        endY = toNode.y;
        const dy = Math.max(30, (endY - startY) * 0.5);
        cp1x = startX;
        cp1y = startY + dy;
        cp2x = endX;
        cp2y = endY - dy;
      } else {
        startX = fromNode.x + fromNode.w;
        startY = fromNode.y + fromNode.h / 2;
        endX = toNode.x;
        endY = toNode.y + toNode.h / 2;
        const dx = Math.max(30, (endX - startX) * 0.55);
        cp1x = startX + dx;
        cp1y = startY;
        cp2x = endX - dx;
        cp2y = endY;
      }

      // Draw Curve
      ctx.beginPath();
      ctx.moveTo(startX, startY);
      ctx.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, endX, endY);
      ctx.strokeStyle = edgeColor;
      ctx.lineWidth = 2.4;
      ctx.stroke();

      // Flowing Pulse Particles
      edge.particles.forEach((p, index) => {
        edge.particles[index] = (p + edge.speed) % 1;
        const t = edge.particles[index];
        const u = 1 - t;

        const px = u * u * u * startX + 3 * u * u * t * cp1x + 3 * u * t * t * cp2x + t * t * t * endX;
        const py = u * u * u * startY + 3 * u * u * t * cp1y + 3 * u * t * t * cp2y + t * t * t * endY;

        ctx.beginPath();
        ctx.arc(px, py, 3.8, 0, Math.PI * 2);
        ctx.fillStyle = fromNode.color;
        ctx.shadowColor = fromNode.color;
        ctx.shadowBlur = 8;
        ctx.fill();
        ctx.shadowBlur = 0;
      });
    });

    // 2. Draw Nodes
    nodes.forEach(node => {
      const radius = 10;

      // Card Shadow
      ctx.shadowColor = isLight ? 'rgba(0, 0, 0, 0.08)' : 'rgba(0, 0, 0, 0.45)';
      ctx.shadowBlur = 14;
      ctx.shadowOffsetY = 4;

      // Card Container
      drawRoundedRect(ctx, node.x, node.y, node.w, node.h, radius);
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
      drawRoundedRect(ctx, node.x, node.y, 4, node.h, 2);
      ctx.fillStyle = node.color;
      ctx.fill();

      // Node Badge
      ctx.font = '600 9.5px JetBrains Mono, monospace';
      ctx.fillStyle = node.color;
      ctx.fillText(node.badge.toUpperCase(), node.x + 12, node.y + (node.h > 60 ? 22 : 18));

      // Node Title
      ctx.font = '600 12.5px Inter, sans-serif';
      ctx.fillStyle = textColor;
      ctx.fillText(node.title, node.x + 12, node.y + (node.h > 60 ? 42 : 36));

      // Circular Handle Pins
      if (node.id !== '1') {
        const pinX = (width < 680) ? (node.x + node.w / 2) : node.x;
        const pinY = (width < 680) ? node.y : (node.y + node.h / 2);
        ctx.beginPath();
        ctx.arc(pinX, pinY, 4.5, 0, Math.PI * 2);
        ctx.fillStyle = node.color;
        ctx.strokeStyle = cardBg;
        ctx.lineWidth = 2;
        ctx.fill();
        ctx.stroke();
      }

      if (node.id !== '5') {
        const pinX = (width < 680) ? (node.x + node.w / 2) : (node.x + node.w);
        const pinY = (width < 680) ? (node.y + node.h) : (node.y + node.h / 2);
        ctx.beginPath();
        ctx.arc(pinX, pinY, 4.5, 0, Math.PI * 2);
        ctx.fillStyle = node.color;
        ctx.strokeStyle = cardBg;
        ctx.lineWidth = 2;
        ctx.fill();
        ctx.stroke();
      }
    });
  }

  // Animation Loop - Exactly One Instance
  let animId = null;
  function loop() {
    drawCanvas();
    animId = requestAnimationFrame(loop);
  }
  loop();

});
