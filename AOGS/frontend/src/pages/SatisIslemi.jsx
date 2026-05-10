import { useEffect, useState } from 'react';
import TopBar from '../components/layout/TopBar';
import { Kart, Buton, Input, Select, Uyari } from '../components/ui/Kontroller';
import { satisApi, aracApi, musteriApi, calisanApi } from '../api/client';
import { formatTl, formatTarih } from '../theme/tokens';

const ODEME_SECENEKLERI = ['Nakit', 'Kredi Kartı', 'Finansman', 'Havale'];

/**
 * Satış İşlemi ekranı (Emre Kuzal'ın modülü).
 * Sol: yeni satış formu. Sağ: satış geçmişi tablosu.
 * Satış başarılı olursa fatura modal'ı açılır.
 *
 * v2: Backend artık SatisYanitDTO döndürüyor — eski nested
 * `s.arac.marka` / `s.musteri.ad` yerine düzleştirilmiş
 * `s.aracMarka` / `s.musteriAdSoyad` alanları kullanılır.
 * Format yardımcıları `theme/tokens.js`'den import edilir.
 */
export default function SatisIslemi() {
  const [satislar, setSatislar] = useState([]);
  const [hata, setHata] = useState(null);
  const [basari, setBasari] = useState(null);
  const [fatura, setFatura] = useState(null);

  const [araclar, setAraclar] = useState([]);
  const [musteriler, setMusteriler] = useState([]);
  const [calisanlar, setCalisanlar] = useState([]);

  const [gonderiyor, setGonderiyor] = useState(false);
  const [form, setForm] = useState({
    aracId: '', musteriTc: '', calisanId: '',
    satisFiyati: '', odemeSekli: 'Nakit'
  });

  const yenile = () => {
    satisApi.liste().then(setSatislar).catch(e => setHata(e.message));
    aracApi.liste().then(a => setAraclar(a.filter(v => v.durum === 'SATISTA'))).catch(console.error);
    musteriApi.liste().then(setMusteriler).catch(console.error);
    calisanApi.liste().then(setCalisanlar).catch(console.error);
  };

  useEffect(() => { yenile(); }, []);

  const guncelle = (alan) => (e) => {
    const val = e.target.value;
    setForm(prev => {
      const yeni = { ...prev, [alan]: val };
      // Araç seçildiğinde fiyatı otomatik doldur
      if (alan === 'aracId' && val) {
        const secilenArac = araclar.find(a => a.aracId === val);
        if (secilenArac) {
          yeni.satisFiyati = secilenArac.satisFiyati;
        }
      }
      return yeni;
    });
  };

  const submit = async (e) => {
    e.preventDefault();
    setHata(null); setBasari(null);

    // Fiyat doğrulama: satış fiyatı alış fiyatının altında olamaz (fixes #11)
    const seciliArac = araclar.find(a => a.aracId === form.aracId);
    if (seciliArac && parseFloat(form.satisFiyati) < seciliArac.alisFiyati) {
      const zarar = (seciliArac.alisFiyati - parseFloat(form.satisFiyati)).toLocaleString('tr-TR');
      setHata(`Satış fiyatı alış fiyatının (${formatTl(seciliArac.alisFiyati)}) altında olamaz. Zarar: ${zarar} ₺`);
      return;
    }

    setGonderiyor(true); // Çift tıklamayı engelle (fixes #12)
    try {
      const yeniSatis = await satisApi.yap({
        ...form,
        satisFiyati: parseFloat(form.satisFiyati),
      });
      setBasari(`Satış başarıyla kaydedildi. ID: ${yeniSatis.satisId}`);
      const faturaMetni = await satisApi.fatura(yeniSatis.satisId);
      setFatura(faturaMetni);
      setForm({ aracId: '', musteriTc: '', calisanId: '', satisFiyati: '', odemeSekli: 'Nakit' });
      yenile();
    } catch (err) {
      setHata(err.message);
    } finally {
      setGonderiyor(false);
    }
  };

  const iptal = async (id) => {
    if (!window.confirm('Satışı iptal etmek istediğinize emin misiniz? (Yalnızca son 24 saat içindekiler iptal edilebilir)')) return;
    try {
      await satisApi.iptal(id);
      setBasari('Satış iptal edildi.');
      yenile();
    } catch (err) {
      setHata(err.message);
    }
  };

  return (
    <>
      <TopBar baslik="Satış İşlemi" aciklama="Yeni satış oluştur ve geçmişi yönet" />
      <div className="p-8 grid grid-cols-1 xl:grid-cols-5 gap-6">
        {/* Form (2/5) */}
        <div className="xl:col-span-2">
          <Kart baslik="Yeni Satış">
            <Uyari tip="hata"   mesaj={hata}   onKapat={() => setHata(null)} />
            <Uyari tip="basari" mesaj={basari} onKapat={() => setBasari(null)} />
            <form onSubmit={submit} className="space-y-3">
              <Select etiket="Araç Seçin *" value={form.aracId} onChange={guncelle('aracId')} required
                      secenekler={[{ deger: '', etiket: '— Satıştaki Araçlar —' },
                                   ...araclar.map(a => ({ deger: a.aracId, etiket: `${a.plakaNo} - ${a.marka} ${a.model} (${formatTl(a.satisFiyati)})` }))]} />
                                   
              <Select etiket="Müşteri Seçin *" value={form.musteriTc} onChange={guncelle('musteriTc')} required
                      secenekler={[{ deger: '', etiket: '— Müşteri Seçin —' },
                                   ...musteriler.map(m => ({ deger: m.tcKimlik, etiket: `${m.ad} ${m.soyad} (TC: ${m.tcKimlik})` }))]} />
                                   
              <Select etiket="Satışı Yapan Çalışan (Opsiyonel)" value={form.calisanId} onChange={guncelle('calisanId')}
                      secenekler={[{ deger: '', etiket: '— Çalışan Seçin —' },
                                   ...calisanlar.map(c => ({ deger: c.calisanId, etiket: `${c.ad} ${c.soyad} (${c.pozisyon})` }))]} />
              <Input etiket="Satış Fiyatı (₺) *" type="number" min={0} step="0.01"
                     value={form.satisFiyati} onChange={guncelle('satisFiyati')} required />
              <Select etiket="Ödeme Şekli"
                      secenekler={ODEME_SECENEKLERI}
                      value={form.odemeSekli} onChange={guncelle('odemeSekli')} />
              <Buton type="submit" disabled={gonderiyor}>
                {gonderiyor ? 'Kaydediliyor...' : 'Satışı Tamamla'}
              </Buton>
            </form>
          </Kart>
        </div>

        {/* Liste (3/5) */}
        <div className="xl:col-span-3">
          <Kart baslik="Satış Geçmişi" aciklama={`Toplam ${satislar.length} satış`} className="overflow-x-auto p-0">
            <table className="w-full text-sm">
              <thead className="bg-aogs-arkaplan border-b border-aogs-kenar">
                <tr className="text-left text-aogs-metin-hafif">
                  <th className="px-4 py-3">Tarih</th>
                  <th className="px-4 py-3">Araç</th>
                  <th className="px-4 py-3">Müşteri</th>
                  <th className="px-4 py-3 text-right">Tutar</th>
                  <th className="px-4 py-3 text-right">Kâr</th>
                  <th className="px-4 py-3">Ödeme</th>
                  <th className="px-4 py-3">İşlem</th>
                </tr>
              </thead>
              <tbody>
                {satislar.map((s) => (
                  <tr key={s.satisId} className="border-b border-aogs-kenar/50">
                    <td className="px-4 py-3 text-xs">{formatTarih(s.satisTarihi)}</td>
                    <td className="px-4 py-3">
                      <div className="font-medium">
                        {s.aracMarka} {s.aracModel}
                      </div>
                      <div className="text-xs text-aogs-metin-hafif">
                        {s.aracPlaka} · {s.aracTipi}
                      </div>
                    </td>
                    <td className="px-4 py-3">
                      {s.musteriAdSoyad}
                    </td>
                    <td className="px-4 py-3 text-right font-semibold text-aogs-birincil">
                      {formatTl(s.satisFiyati)}
                    </td>
                    <td className="px-4 py-3 text-right text-aogs-basari font-medium">
                      {formatTl(s.kar)}
                    </td>
                    <td className="px-4 py-3 text-xs">{s.odemeSekli}</td>
                    <td className="px-4 py-3">
                      <button
                        onClick={() => iptal(s.satisId)}
                        className="text-aogs-uyari hover:underline text-xs"
                      >İptal</button>
                    </td>
                  </tr>
                ))}
                {!satislar.length && (
                  <tr><td colSpan={7} className="text-center py-8 text-aogs-metin-hafif">Henüz satış yok.</td></tr>
                )}
              </tbody>
            </table>
          </Kart>
        </div>
      </div>

      {/* Fatura Modal */}
      {fatura && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4"
             onClick={() => setFatura(null)}>
          <div className="bg-white rounded-xl shadow-xl max-w-lg w-full p-6"
               onClick={(e) => e.stopPropagation()}>
            <h3 className="text-xl font-bold text-aogs-birincil mb-3">Fatura</h3>
            <pre className="bg-aogs-arkaplan p-4 rounded text-xs whitespace-pre-wrap">{fatura}</pre>
            <div className="flex justify-end gap-2 mt-4">
              <Buton tip="ikincil" onClick={() => navigator.clipboard.writeText(fatura)}>
                Kopyala
              </Buton>
              <Buton onClick={() => setFatura(null)}>Kapat</Buton>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
