import { Routes, Route, Navigate } from 'react-router-dom';
import Sidebar from './components/layout/Sidebar';

import Dashboard       from './pages/Dashboard';
import AracListesi     from './pages/AracListesi';
import AracEkle        from './pages/AracEkle';
import MusteriYonetimi from './pages/MusteriYonetimi';
import CalisanYonetimi from './pages/CalisanYonetimi';
import SatisIslemi     from './pages/SatisIslemi';
import Ekspertiz       from './pages/Ekspertiz';
import SigortaMuayene  from './pages/SigortaMuayene';
import Raporlama       from './pages/Raporlama';

/**
 * Uygulama kökü. Sabit sidebar + dinamik içerik alanı.
 * PDR §3.4.4'teki StackPane yerini React Router üstlenir;
 * URL bazlı navigasyon ile her ekran kendi rotasında yaşar.
 */
export default function App() {
  return (
    <div className="flex min-h-screen bg-aogs-arkaplan">
      <Sidebar />
      <main className="flex-1 flex flex-col">
        <Routes>
          <Route path="/"                element={<Dashboard />} />
          <Route path="/araclar"         element={<AracListesi />} />
          <Route path="/araclar/ekle"    element={<AracEkle />} />
          <Route path="/araclar/duzenle/:id" element={<AracEkle />} />
          <Route path="/musteriler"      element={<MusteriYonetimi />} />
          <Route path="/calisanlar"      element={<CalisanYonetimi />} />
          <Route path="/satislar"        element={<SatisIslemi />} />
          <Route path="/ekspertiz"       element={<Ekspertiz />} />
          <Route path="/sigorta-muayene" element={<SigortaMuayene />} />
          <Route path="/raporlar"        element={<Raporlama />} />
          <Route path="*"                element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  );
}
