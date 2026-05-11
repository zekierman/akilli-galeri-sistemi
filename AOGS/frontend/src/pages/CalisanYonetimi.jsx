import { useEffect, useState } from 'react';
import TopBar from '../components/layout/TopBar';
import { Kart, Buton, Input, Uyari } from '../components/ui/Kontroller';
import { calisanApi } from '../api/client';

export default function CalisanYonetimi() {
  const [calisanlar, setCalisanlar] = useState([]);
  const [hata, setHata] = useState(null);
  const [basari, setBasari] = useState(null);
  
  const [duzenlenenId, setDuzenlenenId] = useState(null);
  const [form, setForm] = useState({
    ad: '', soyad: '', tcKimlik: '', pozisyon: '', maas: '', iseBaslamaTarihi: ''
  });

  const parseNumber = (value) => {
    if (value == null || value === '') return 0;
    const normalized = String(value).replace(/\./g, '').replace(/,/g, '.');
    return parseFloat(normalized) || 0;
  };

  const yenile = () => calisanApi.liste().then(setCalisanlar).catch(e => setHata(e.message));

  useEffect(() => { yenile(); }, []);

  const guncelle = (alan) => (e) => setForm({ ...form, [alan]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setHata(null); setBasari(null);
    try {
      const payload = { ...form, maas: parseNumber(form.maas) };
      
      if (duzenlenenId) {
        await calisanApi.guncelle(duzenlenenId, payload);
        setBasari('Çalışan başarıyla güncellendi.');
      } else {
        await calisanApi.ekle(payload);
        setBasari('Yeni çalışan başarıyla eklendi.');
      }
      
      setForm({ ad: '', soyad: '', tcKimlik: '', pozisyon: '', maas: '', iseBaslamaTarihi: '' });
      setDuzenlenenId(null);
      yenile();
    } catch (err) {
      setHata(err.message);
    }
  };

  const sil = async (id) => {
    if (!window.confirm('Çalışanı silmek istediğinize emin misiniz?')) return;
    try {
      await calisanApi.sil(id);
      setBasari('Çalışan başarıyla silindi.');
      yenile();
    } catch (err) {
      setHata(err.message);
    }
  };

  const duzenleModunaGec = (c) => {
    setDuzenlenenId(c.calisanId);
    setForm({
      ad: c.ad,
      soyad: c.soyad,
      tcKimlik: c.tcKimlik,
      pozisyon: c.pozisyon || '',
      maas: c.maas || '',
      iseBaslamaTarihi: c.iseBaslamaTarihi || ''
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const iptalEt = () => {
    setDuzenlenenId(null);
    setForm({ ad: '', soyad: '', tcKimlik: '', pozisyon: '', maas: '', iseBaslamaTarihi: '' });
  };

  return (
    <>
      <TopBar baslik="Çalışan Yönetimi" aciklama="Personel listesi ve kayıt işlemleri" />
      <div className="p-8 grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Sol Kolon - Form */}
        <div className="lg:col-span-1">
          <Kart baslik={duzenlenenId ? "Çalışan Düzenle" : "Yeni Çalışan Ekle"}>
            <Uyari tip="hata" mesaj={hata} onKapat={() => setHata(null)} />
            <Uyari tip="basari" mesaj={basari} onKapat={() => setBasari(null)} />
            
            <form onSubmit={submit} className="space-y-3">
              <Input etiket="Ad *" value={form.ad} onChange={guncelle('ad')} required />
              <Input etiket="Soyad *" value={form.soyad} onChange={guncelle('soyad')} required />
              <Input etiket="TC Kimlik *" value={form.tcKimlik} onChange={guncelle('tcKimlik')} 
                     maxLength={11} required disabled={!!duzenlenenId} />
              <Input etiket="Pozisyon" value={form.pozisyon} onChange={guncelle('pozisyon')} />
              <Input etiket="Maaş (₺)" type="number" step="0.01" value={form.maas} onChange={guncelle('maas')} />
              <Input etiket="İşe Başlama Tarihi" type="date" value={form.iseBaslamaTarihi} onChange={guncelle('iseBaslamaTarihi')} />
              
              <div className="flex gap-2 pt-2">
                <Buton type="submit" className="flex-1">
                  {duzenlenenId ? 'Güncelle' : 'Kaydet'}
                </Buton>
                {duzenlenenId && (
                  <Buton type="button" tip="ikincil" onClick={iptalEt}>İptal</Buton>
                )}
              </div>
            </form>
          </Kart>
        </div>

        {/* Sağ Kolon - Liste */}
        <div className="lg:col-span-2">
          <Kart baslik="Çalışan Listesi" aciklama={`Toplam ${calisanlar.length} çalışan`} className="overflow-x-auto p-0">
            <table className="w-full text-sm">
              <thead className="bg-aogs-arkaplan border-b border-aogs-kenar">
                <tr className="text-left text-aogs-metin-hafif">
                  <th className="px-4 py-3">Personel No</th>
                  <th className="px-4 py-3">Ad Soyad</th>
                  <th className="px-4 py-3">Pozisyon</th>
                  <th className="px-4 py-3">Maaş</th>
                  <th className="px-4 py-3">Başlama Tarihi</th>
                  <th className="px-4 py-3">İşlem</th>
                </tr>
              </thead>
              <tbody>
                {calisanlar.map((c) => (
                  <tr key={c.calisanId} className="border-b border-aogs-kenar/50 hover:bg-aogs-arkaplan/50 transition-colors">
                    <td className="px-4 py-3 font-mono text-xs">{c.calisanId}</td>
                    <td className="px-4 py-3 font-medium">{c.ad} {c.soyad}</td>
                    <td className="px-4 py-3">{c.pozisyon || '-'}</td>
                    <td className="px-4 py-3">
                      {c.maas ? `${Number(c.maas).toLocaleString('tr-TR')} ₺` : '-'}
                    </td>
                    <td className="px-4 py-3 text-xs">{c.iseBaslamaTarihi || '-'}</td>
                    <td className="px-4 py-3 flex gap-3">
                      <button type="button" onClick={() => duzenleModunaGec(c)} className="text-aogs-birincil hover:underline text-xs font-medium">
                        Düzenle
                      </button>
                      <button type="button" onClick={() => sil(c.calisanId)} className="text-aogs-uyari hover:underline text-xs">
                        Sil
                      </button>
                    </td>
                  </tr>
                ))}
                {!calisanlar.length && (
                  <tr><td colSpan={6} className="text-center py-8 text-aogs-metin-hafif">Sistemde kayıtlı çalışan bulunmamaktadır.</td></tr>
                )}
              </tbody>
            </table>
          </Kart>
        </div>
      </div>
    </>
  );
}
