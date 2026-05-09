import { NavLink } from 'react-router-dom';

/**
 * Sabit sol navigasyon menüsü (PDR §1.2.1 — Sabit Sidebar Navigasyonu).
 * <p>
 * Eski JavaFX VBox'ın yerini tutar; aktif sayfa NavLink'in
 * isActive prop'u ile renk değişimi alır.
 */
const menuOgeleri = [
  { yol: '/',                ad: 'Dashboard',          ikon: '🏠' },
  { yol: '/araclar',         ad: 'Araç Listesi',       ikon: '🚗' },
  { yol: '/araclar/ekle',    ad: 'Araç Ekle',          ikon: '➕' },
  { yol: '/musteriler',      ad: 'Müşteri Yönetimi',   ikon: '👥' },
  { yol: '/calisanlar',      ad: 'Çalışan Yönetimi',   ikon: '💼' },
  { yol: '/satislar',        ad: 'Satış İşlemi',       ikon: '💳' },
  { yol: '/ekspertiz',       ad: 'Ekspertiz',          ikon: '🔍' },
  { yol: '/sigorta-muayene', ad: 'Sigorta/Muayene',    ikon: '📋' },
  { yol: '/raporlar',        ad: 'Raporlama',          ikon: '📊' },
];

export default function Sidebar() {
  return (
    <aside className="w-60 bg-aogs-sidebar text-white h-screen sticky top-0 flex flex-col">
      {/* Logo */}
      <div className="px-6 py-6 border-b border-white/10">
        <div className="flex items-center gap-2">
          <span className="text-3xl">🚗</span>
          <div>
            <div className="font-bold text-lg leading-tight">AOGS</div>
            <div className="text-xs text-white/60">Akıllı Galeri</div>
          </div>
        </div>
      </div>

      {/* Menü öğeleri */}
      <nav className="flex-1 py-4 overflow-y-auto">
        {menuOgeleri.map((m) => (
          <NavLink
            key={m.yol}
            to={m.yol}
            end={m.yol === '/'}
            className={({ isActive }) =>
              `flex items-center gap-3 px-6 py-3 text-sm transition-colors ` +
              (isActive
                ? 'bg-aogs-birincil text-white font-medium border-l-4 border-white'
                : 'text-white/80 hover:bg-white/5 hover:text-white border-l-4 border-transparent')
            }
          >
            <span className="text-lg">{m.ikon}</span>
            <span>{m.ad}</span>
          </NavLink>
        ))}
      </nav>

      {/* Alt — kullanıcı bilgisi */}
      <div className="px-6 py-4 border-t border-white/10 text-xs text-white/60">
        <div className="font-medium text-white/90">AOGS Admin</div>
        <div>Sistem Yöneticisi</div>
      </div>
    </aside>
  );
}
