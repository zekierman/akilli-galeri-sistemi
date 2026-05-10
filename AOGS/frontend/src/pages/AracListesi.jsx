import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import TopBar from '../components/layout/TopBar';
import { Buton, Input, DurumRozeti, Uyari } from '../components/ui/Kontroller';
import { aracApi } from '../api/client';

export default function AracListesi() {
  const [araclar, setAraclar] = useState([]);
  const [arama, setArama]     = useState('');
  const [hata, setHata]       = useState(null);

  const yenile = () => {
    aracApi.liste().then(setAraclar).catch((e) => setHata(e.message));
  };

  useEffect(() => { yenile(); }, []);

  const filtrelenmis = araclar.filter((a) => {
    const q = arama.toLowerCase();
    return !q
      || a.marka?.toLowerCase().includes(q)
      || a.model?.toLowerCase().includes(q)
      || a.plakaNo?.toLowerCase().includes(q);
  });

  const sil = async (id) => {
    if (!window.confirm('Aracı silmek istediğinizden emin misiniz?')) return;
    try {
      await aracApi.sil(id);
      yenile();
    } catch (e) {
      setHata(e.message);
    }
  };

  return (
    <>
      <TopBar
        baslik="Araç Listesi"
        aciklama={`Toplam ${araclar.length} araç`}
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
          />
        </div>

        <div className="aogs-kart overflow-x-auto p-0">
          <table className="w-full text-sm">
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
              {filtrelenmis.map((a) => (
                <tr key={a.aracId} className="border-b border-aogs-kenar/50 hover:bg-aogs-arkaplan/40">
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
                      className="text-aogs-uyari hover:underline text-xs"
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
            </tbody>
          </table>
        </div>
      </div>
    </>
  );
}
