import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import TopBar from '../components/layout/TopBar';
import { Buton, Input, DurumRozeti, Uyari } from '../components/ui/Kontroller'; 
import { aracApi } from '../api/client';

export default function AracListesi() {
  const [araclar, setAraclar] = useState([]);
  const [arama, setArama]     = useState('');
  const [hata, setHata]       = useState(null);
  // 1. ADIM: Loading state ekle
  const [yukleniyor, setYukleniyor] = useState(false);

  // 2. ADIM: Yenile fonksiyonunu loading state ile güncelle
  const yenile = async () => {
    setYukleniyor(true);
    try {
      const data = await aracApi.liste();
      setAraclar(data);
    } catch (e) {
      setHata(e.message);
    } finally {
      setYukleniyor(false);
    }
  };

  useEffect(() => { yenile(); }, []);

  // ... (filtrelenmis ve sil fonksiyonları aynı kalıyor)

  return (
    <>
      <TopBar
        baslik="Araç Listesi"
        aciklama={yukleniyor ? "Güncelleniyor..." : `Toplam ${araclar.length} araç`} // Küçük bir UX dokunuşu
        aksiyon={
          <Link to="/araclar/ekle">
            <Buton>+ Yeni Araç</Buton>
          </Link>
        }
      />
      <div className="p-8">
        <Uyari tip="hata" mesaj={hata} onKapat={() => setHata(null)} />

        <div className="mb-4 max-w-md">
          <Input
            placeholder="Marka, model veya plaka ara..."
            value={arama}
            onChange={(e) => setArama(e.target.value)}
            disabled={yukleniyor} // Yüklenirken arama yapılmasın
          />
        </div>

        <div className="aogs-kart overflow-x-auto p-0">
          <table className="w-full text-sm">
            {/* ... thead aynı kalıyor ... */}
            <thead className="bg-aogs-arkaplan border-b border-aogs-kenar">
              <tr className="text-left text-aogs-metin-hafif">
                <th className="px-4 py-3 font-semibold">ID / Plaka</th>
                <th className="px-4 py-3 font-semibold">Marka / Model</th>
                <th className="px-4 py-3 font-semibold">Yıl</th>
                <th className="px-4 py-3 font-semibold">KM</th>
                <th className="px-4 py-3 font-semibold">Yakıt</th>
                <th className="px-4 py-3 font-semibold">Fiyat</th>
                <th className="px-4 py-3 font-semibold">Durum</th>
                <th className="px-4 py-3 font-semibold">İşlem</th>
              </tr>
            </thead>
            <tbody>
              {/* 3. ADIM: Yükleme durumuna göre satırları render et */}
              {yukleniyor ? (
                <tr>
                  <td colSpan={8} className="text-center py-20">
                    <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-aogs-birincil"></div>
                    <p className="mt-2 text-aogs-metin-hafif">Araçlar yükleniyor...</p>
                  </td>
                </tr>
              ) : (
                <>
                  {filtrelenmis.map((a) => (
                    <tr key={a.aracId} className="border-b border-aogs-kenar/50 hover:bg-aogs-arkaplan/40">
                      {/* ... tr içeriği aynı ... */}
                      <td className="px-4 py-3 font-mono">
                        <div className="text-[10px] text-aogs-metin-hafif mb-0.5 select-all cursor-pointer" title="ID'yi kopyalamak için çift tıklayın">{a.aracId}</div>
                        <div className="font-semibold">{a.plakaNo}</div>
                      </td>
                      <td className="px-4 py-3">
                        <div className="font-medium">{a.marka} {a.model}</div>
                        <div className="text-xs text-aogs-metin-hafif">{a.aracTipi ?? ''}</div>
                      </td>
                      <td className="px-4 py-3">{a.yil}</td>
                      <td className="px-4 py-3">{Number(a.km).toLocaleString('tr-TR')} km</td>
                      <td className="px-4 py-3">{a.yakitTipi}</td>
                      <td className="px-4 py-3 font-semibold text-aogs-birincil">
                        {Number(a.satisFiyati).toLocaleString('tr-TR')} ₺
                      </td>
                      <td className="px-4 py-3"><DurumRozeti durum={a.durum} /></td>
                      <td className="px-4 py-3 flex gap-3">
                        <Link to={`/araclar/duzenle/${a.aracId}`} className="text-aogs-birincil hover:underline text-xs font-medium">
                          Düzenle
                        </Link>
                        <button
                          onClick={() => sil(a.aracId)}
                          disabled={yukleniyor} // Silme işlemi sırasında butonlar pasif olsun
                          className="text-aogs-uyari hover:underline text-xs disabled:opacity-50"
                        >Sil</button>
                      </td>
                    </tr>
                  ))}
                  {!filtrelenmis.length && (
                    <tr>
                      <td colSpan={8} className="text-center py-12 text-aogs-metin-hafif">
                        {arama ? 'Eşleşme bulunamadı.' : 'Henüz araç eklenmemiş.'}
                      </td>
                    </tr>
                  )}
                </>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </>
  );
}
