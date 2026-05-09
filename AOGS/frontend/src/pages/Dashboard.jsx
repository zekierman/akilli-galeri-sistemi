import { useEffect, useState } from 'react';
import TopBar from '../components/layout/TopBar';
import { Kart, IstatistikKarti } from '../components/ui/Kontroller';
import { raporApi, bildirimApi } from '../api/client';
import { formatTl, formatSayi } from '../theme/tokens';

/**
 * Dashboard ekranı (PDR §3.4.4 - Dashboard).
 * Backend'den /api/raporlar/ozet ve /api/bildirimler/dashboard
 * uç noktalarından veri çeker.
 *
 * v2: Format yardımcıları `theme/tokens.js`'den import edilir;
 * sayfa içinde yerel format fonksiyonu yoktur.
 */
export default function Dashboard() {
  const [ozet, setOzet] = useState(null);
  const [bildirimler, setBildirimler] = useState(null);
  const [hata, setHata] = useState(null);

  useEffect(() => {
    Promise.all([raporApi.ozet(), bildirimApi.dashboard()])
      .then(([o, b]) => { setOzet(o); setBildirimler(b); })
      .catch((err) => setHata(err.message));
  }, []);

  return (
    <>
      <TopBar
        baslik="Dashboard"
        aciklama="Galeri performans özeti ve yaklaşan bildirimler"
      />
      <div className="p-8 space-y-6">
        {hata && <div className="text-aogs-uyari">⚠️ {hata}</div>}

        {/* İstatistik Kartları */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <IstatistikKarti
            baslik="Satıştaki Araçlar"
            deger={formatSayi(ozet?.satistaArac) ?? '—'}
            alt={`Toplam stok: ${formatTl(ozet?.stokDeger)}`}
          />
          <IstatistikKarti
            baslik="Toplam Satış"
            deger={formatSayi(ozet?.satisAdet) ?? '—'}
            vurguluRenk="basari"
            alt="Tüm zamanların"
          />
          <IstatistikKarti
            baslik="Toplam Ciro"
            deger={formatTl(ozet?.toplamCiro)}
            vurguluRenk="basari"
          />
          <IstatistikKarti
            baslik="Toplam Kâr"
            deger={formatTl(ozet?.toplamKar)}
            vurguluRenk="birincil"
          />
        </div>

        {/* Bildirimler */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
          <Kart
            baslik="🔔 Yaklaşan Sigortalar (30 gün)"
            aciklama="Süresi yaklaşan trafik sigortaları"
          >
            <BildirimListesi
              kayitlar={bildirimler?.yaklasanSigortalar ?? []}
              etiket={(s) => `${s.policeNo} — ${s.kalanGun} gün kaldı`}
              bos="Yaklaşan sigorta yok."
            />
          </Kart>
          <Kart
            baslik="🚦 Yaklaşan Muayeneler (30 gün)"
            aciklama="TÜVTÜRK muayene tarihleri"
          >
            <BildirimListesi
              kayitlar={bildirimler?.yaklasanMuayeneler ?? []}
              etiket={(m) => `Araç ${m.arac?.plakaNo ?? '-'} — ${m.kalanGun} gün`}
              bos="Yaklaşan muayene yok."
            />
          </Kart>
        </div>
      </div>
    </>
  );
}

function BildirimListesi({ kayitlar, etiket, bos }) {
  if (!kayitlar.length) {
    return <p className="text-sm text-aogs-metin-hafif">{bos}</p>;
  }
  return (
    <ul className="space-y-2">
      {kayitlar.map((k, i) => (
        <li key={i} className="flex items-center gap-2 text-sm border-l-4 border-aogs-birincil pl-3 py-1">
          <span>📌</span>
          <span>{etiket(k)}</span>
        </li>
      ))}
    </ul>
  );
}
