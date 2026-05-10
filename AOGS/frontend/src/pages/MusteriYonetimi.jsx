import { useEffect, useState } from 'react';
import TopBar from '../components/layout/TopBar';
import { Kart, Buton, Input, Uyari } from '../components/ui/Kontroller';
import { musteriApi, satisApi } from '../api/client';
import { formatTl, formatTarih } from '../theme/tokens';

export default function MusteriYonetimi() {
  const [musteriler, setMusteriler] = useState([]);
  const [arama, setArama] = useState('');
  const [hata, setHata] = useState(null);
  const [basari, setBasari] = useState(null);
  const [editId, setEditId] = useState(null);

  const bosForm = { ad: '', soyad: '', tcKimlik: '', telefon: '', email: '', adres: '', notlar: '' };
  const [form, setForm] = useState(bosForm);

  const [gecmisMusteri, setGecmisMusteri] = useState(null);
  const [gecmisSatislar, setGecmisSatislar] = useState([]);

  const yenile = () => musteriApi.liste().then(setMusteriler).catch(e => setHata(e.message));

  useEffect(() => { yenile(); }, []);

  const submit = async (e) => {
    e.preventDefault();
    setHata(null); setBasari(null);
    try {
      if (editId) {
        await musteriApi.guncelle(editId, form);
        setBasari('Müşteri güncellendi.');
        setEditId(null);
      } else {
        await musteriApi.ekle(form);
        setBasari('Müşteri eklendi.');
      }
      setForm(bosForm);
      yenile();
    } catch (err) {
      setHata(err.message);
    }
  };

  const iptalEt = () => {
    setEditId(null);
    setForm(bosForm);
    setHata(null);
  };

  const duzenle = (m) => {
    setEditId(m.musteriId);
    setForm({
      ad: m.ad, soyad: m.soyad, tcKimlik: m.tcKimlik,
      telefon: m.telefon || '', email: m.email || '',
      adres: m.adres || '', notlar: m.notlar || ''
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const sil = async (id) => {
    if (!window.confirm('Bu müşteriyi silmek istediğinize emin misiniz? (Geçmiş işlemi olan müşteriler silinemez)')) return;
    try {
      await musteriApi.sil(id);
      setBasari('Müşteri başarıyla silindi.');
      yenile();
    } catch (e) {
      setHata(e.message);
    }
  };

  const gecmisGoster = async (m) => {
    try {
      const satislar = await satisApi.musteriSatislari(m.tcKimlik);
      setGecmisSatislar(satislar);
      setGecmisMusteri(m);
    } catch (e) {
      setHata("Satış geçmişi alınamadı: " + e.message);
    }
  };

  const filtrelenmis = musteriler.filter((m) => {
    const q = arama.toLowerCase();
    return !q
      || m.ad?.toLowerCase().includes(q)
      || m.soyad?.toLowerCase().includes(q)
      || m.tcKimlik?.includes(q);
  });

  const guncelle = (alan) => (e) => setForm({ ...form, [alan]: e.target.value });

  return (
    <>
      <TopBar baslik="Müşteri Yönetimi" aciklama={`Toplam ${musteriler.length} müşteri`} />
      <div className="p-8 grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Form */}
        <div className="lg:col-span-1">
          <Kart baslik={editId ? "Müşteri Düzenle" : "Yeni Müşteri"}>
            <Uyari tip="hata"   mesaj={hata}   onKapat={() => setHata(null)} />
            <Uyari tip="basari" mesaj={basari} onKapat={() => setBasari(null)} />
            <form onSubmit={submit} className="space-y-3">
              <Input etiket="Ad *"      value={form.ad}      onChange={guncelle('ad')}      required />
              <Input etiket="Soyad *"   value={form.soyad}   onChange={guncelle('soyad')}   required />
              <Input etiket="TC Kimlik *" maxLength={11} value={form.tcKimlik} onChange={guncelle('tcKimlik')} required disabled={!!editId} />
              <Input etiket="Telefon"   value={form.telefon} onChange={guncelle('telefon')} />
              <Input etiket="Email"     type="email" value={form.email} onChange={guncelle('email')} />
              <Input etiket="Adres"     value={form.adres}   onChange={guncelle('adres')} />
              <Input etiket="Not"       value={form.notlar}  onChange={guncelle('notlar')} />
              <div className="flex gap-2 pt-2">
                <Buton type="submit">{editId ? "Güncelle" : "Kaydet"}</Buton>
                {editId && <Buton type="button" tip="ikincil" onClick={iptalEt}>İptal</Buton>}
              </div>
            </form>
          </Kart>
        </div>

        {/* Liste */}
        <div className="lg:col-span-2">
          <Kart baslik="Müşteri Listesi" className="overflow-x-auto p-0">
            <div className="px-6 pt-4">
              <Input
                placeholder="Ad, soyad veya TC ara..."
                value={arama}
                onChange={(e) => setArama(e.target.value)}
              />
            </div>
            <table className="w-full text-sm mt-4">
              <thead className="bg-aogs-arkaplan border-b border-aogs-kenar">
                <tr className="text-left text-aogs-metin-hafif">
                  <th className="px-4 py-3">Ad Soyad</th>
                  <th className="px-4 py-3">TC</th>
                  <th className="px-4 py-3">Telefon</th>
                  <th className="px-4 py-3">Email</th>
                  <th className="px-4 py-3 w-1/4">Notlar</th>
                  <th className="px-4 py-3">İşlem</th>
                </tr>
              </thead>
              <tbody>
                {filtrelenmis.map((m) => (
                  <tr key={m.musteriId} className="border-b border-aogs-kenar/50 hover:bg-aogs-arkaplan/40">
                    <td className="px-4 py-3 font-medium">{m.ad} {m.soyad}</td>
                    <td className="px-4 py-3 font-mono text-xs">{m.tcKimlik}</td>
                    <td className="px-4 py-3">{m.telefon ?? '-'}</td>
                    <td className="px-4 py-3 text-aogs-metin-hafif">{m.email ?? '-'}</td>
                    <td className="px-4 py-3 text-xs text-aogs-metin-hafif truncate max-w-[150px]" title={m.notlar}>{m.notlar || '-'}</td>
                    <td className="px-4 py-3 flex gap-3">
                      <button
                        onClick={() => duzenle(m)}
                        className="text-aogs-birincil hover:underline text-xs font-medium"
                      >Düzenle</button>
                      <button
                        onClick={() => sil(m.musteriId)}
                        className="text-aogs-uyari hover:underline text-xs"
                      >Sil</button>
                      <button
                        onClick={() => gecmisGoster(m)}
                        className="text-aogs-ikincil hover:underline text-xs font-medium"
                      >Geçmiş</button>
                    </td>
                  </tr>
                ))}
                {!filtrelenmis.length && (
                  <tr><td colSpan={6} className="text-center py-8 text-aogs-metin-hafif">Müşteri bulunamadı.</td></tr>
                )}
              </tbody>
            </table>
          </Kart>
        </div>
      </div>

      {/* Satış Geçmişi Modal */}
      {gecmisMusteri && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4" onClick={() => setGecmisMusteri(null)}>
          <div className="bg-white rounded-xl shadow-xl max-w-3xl w-full p-6" onClick={(e) => e.stopPropagation()}>
            <h3 className="text-xl font-bold text-aogs-birincil mb-1">{gecmisMusteri.ad} {gecmisMusteri.soyad}</h3>
            <p className="text-sm text-aogs-metin-hafif mb-4">Müşteri Satın Alma Geçmişi</p>
            
            <div className="max-h-96 overflow-y-auto">
              <table className="w-full text-sm">
                <thead className="bg-aogs-arkaplan border-b border-aogs-kenar sticky top-0">
                  <tr className="text-left text-aogs-metin-hafif">
                    <th className="px-4 py-2">Tarih</th>
                    <th className="px-4 py-2">Araç</th>
                    <th className="px-4 py-2">Ödeme Şekli</th>
                    <th className="px-4 py-2 text-right">Tutar</th>
                  </tr>
                </thead>
                <tbody>
                  {gecmisSatislar.map((s) => (
                    <tr key={s.satisId} className="border-b border-aogs-kenar/50">
                      <td className="px-4 py-2">{formatTarih(s.satisTarihi)}</td>
                      <td className="px-4 py-2">{s.aracMarka} {s.aracModel} ({s.aracPlaka})</td>
                      <td className="px-4 py-2">{s.odemeSekli}</td>
                      <td className="px-4 py-2 text-right font-medium text-aogs-birincil">{formatTl(s.satisFiyati)}</td>
                    </tr>
                  ))}
                  {!gecmisSatislar.length && (
                    <tr><td colSpan={4} className="text-center py-4 text-aogs-metin-hafif">Bu müşterinin henüz satın aldığı bir araç yok.</td></tr>
                  )}
                </tbody>
              </table>
            </div>
            <div className="mt-4 flex justify-end">
              <Buton onClick={() => setGecmisMusteri(null)}>Kapat</Buton>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
