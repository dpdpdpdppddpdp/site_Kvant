document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('modal');
    const openBtns = document.querySelectorAll('.open-modal-trigger');
    const closeBtn = document.querySelector('.close');
    const form = document.getElementById('leadForm');

    // Маска телефона
    const phoneInput = document.getElementById('phone');
    if (phoneInput) {
        IMask(phoneInput, { mask: '+{7} (000) 000-00-00', lazy: false });
    }

    function openModal() { modal.classList.add('active'); document.body.style.overflow = 'hidden'; }
    function closeModal() { modal.classList.remove('active'); document.body.style.overflow = ''; }

    openBtns.forEach(btn => btn.addEventListener('click', openModal));
    closeBtn.addEventListener('click', closeModal);
    window.addEventListener('click', (e) => { if (e.target === modal) closeModal(); });

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const firstName = document.getElementById('firstName').value.trim();
        const phone = phoneInput.value;
        const email = document.getElementById('email').value.trim();
        const serviceType = document.getElementById('serviceType').value;
        const comment = document.getElementById('comment').value;

        if (!firstName) { alert('Введите имя'); return; }
        if (!phone || phone.replace(/\D/g,'').length < 10) { alert('Введите корректный телефон'); return; }
        if (!email || !/^[^\s@]+@([^\s@]+\.)+[^\s@]+$/.test(email)) { alert('Введите корректный email'); return; }
        if (!serviceType) { alert('Выберите услугу'); return; }

        const params = new URLSearchParams(window.location.search);
        const payload = {
            firstName, lastName: '', phone, email, serviceType, comment,
            utmSource: params.get('utm_source') || '',
            utmCampaign: params.get('utm_campaign') || '',
            utmMedium: params.get('utm_medium') || '',
            utmContent: params.get('utm_content') || '',
            referrerUrl: document.referrer || ''
        };

        try {
            const response = await fetch('/api/leads', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const result = await response.json();
            if (response.ok) {
                alert(result.message);
                form.reset();
                closeModal();
            } else {
                alert('Ошибка: ' + (result.message || 'попробуйте позже'));
            }
        } catch (err) {
            alert('Не удалось отправить заявку. Проверьте соединение с сервером.');
        }
    });

    // Sticky header тень
    const header = document.getElementById('stickyHeader');
    window.addEventListener('scroll', () => {
        if (window.scrollY > 10) header.classList.add('scrolled');
        else header.classList.remove('scrolled');
    });

    // Анимации появления (Intersection Observer)
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, { threshold: 0.1 });
    document.querySelectorAll('.adv-card, .project-card, .hero-content, .hero-image').forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(20px)';
        el.style.transition = 'opacity 0.5s, transform 0.5s';
        observer.observe(el);
    });
});