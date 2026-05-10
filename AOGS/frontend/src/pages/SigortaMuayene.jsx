import { useEffect, useState } from 'react';
import TopBar from '../components/layout/TopBar';
import { Kart, Uyari, Input, Select, Buton } from '../components/ui/Kontroller';
import { bildirimApi, aracApi } from '../api/client';

export default function SigortaMuayene() {
  const [veri, setVeri] = useState(null);
  const [araclar, setAraclar] = useState([]);
  const [hata, setHata] = useState(null);
  const [basari, setBasari] = useState(null);

  const [aktifTab, setAktifTab] = useState('sigorta');
  const bosSForm = { aracId: '', policeNo: '', sigortaSirketi: '', baslangicTarihi: '', bitisTarihi: '', primTutari: '' };
  const bosMForm = { aracId: '', muayeneIstasyonu: '', muayeneTarihi: '', sonrakiMuayeneTarihi: '', gectiMi: true, notlar: '' };
  const [sForm, setSForm] = useState(bosSForm);
  const [mForm, setMForm] = useState(bosMForm);
  const [sDuzenleId, setSDuzenleId] = useState(null);
  const [mDuzenleId, setMDuzenleId] = useState(null);

  const yenile = () => {
    bildirimApi.dashboard().then(setVeri).catch(e => setHata(e.message));
  };

  useEffect(() => {
    yenile();
    aracApi.liste().then(setAraclar).catch(e => console.error('Araçlar çekilemedi:', e.message));
  }, []);

  const getAracTipi = (aracId) => {
    const arac = araclar.find((a) => a.aracId === aracId);
    if (!arac) return 'OTOMOBIL';
    
    // Araç tipini property'lere göre belirle
    if (arac.motorHacmi !== undefined) return 'MOTOSIKLET';
    if (arac.dortCeker !== undefined) return 'SUV';
    if (arac.sunroof !== undefined) return 'OTOMOBIL';
    if (arac.frigorifik !== undefined) return 'TICARI';
    
    // Varsayılan olarak OTOMOBIL
    return 'OTOMOBIL';
  };

  const sigortaKaydet = async (e) => {
    e.preventDefault();
    setHata(null); setBasari(null);
    try {
      const payload = {
        ...sForm,
        primTutari: parseFloat(sForm.primTutari || 0),
        arac: {
          aracId: sForm.aracId,
          '@type': getAracTipi(sForm.aracId)
        }
      };
      if (sDuzenleId) {
        await bildirimApi.sigortaGuncelle(sDuzenleId, payload);
        setBasari('Sigorta başarıyla güncellendi.');
        setSDuzenleId(null);
      } else {
        await bildirimApi.sigortaEkle(payload);
        setBasari('Sigorta başarıyla kaydedildi.');
      }
      setSForm(bosSForm);
      yenile();
    } catch (err) { setHata(err.message); }
  };

  const muayeneKaydet = async (e) => {
    e.preventDefault();
    setHata(null); setBasari(null);
    try {
      const payload = {
        ...mForm,
        arac: {
          aracId: mForm.aracId,
          '@type': getAracTipi(mForm.aracId)
        }
      };
      if (mDuzenleId) {
        await bildirimApi.muayeneGuncelle(mDuzenleId, payload);
        setBasari('Muayene başarıyla güncellendi.');
        setMDuzenleId(null);
      } else {
        await bildirimApi.muayeneEkle(payload);
        setBasari('Muayene başarıyla kaydedildi.');
      }
      setMForm(bosMForm);
      yenile();
    } catch (err) { setHata(err.message); }
  };

  const sigortaSil = async (id) => {
    if(!window.confirm('Sigorta kaydını silmek istediğinize emin misiniz?')) return;
    try {
      await bildirimApi.sigortaSil(id);
      setBasari('Sigorta silindi.');
      yenile();
    } catch(e) { setHata(e.message); }
  };

  const muayeneSil = async (id) => {
    if(!window.confirm('Muayene kaydını silmek istediğinize emin misiniz?')) return;
    try {
      await bildirimApi.muayeneSil(id);
      setBasari('Muayene silindi.');
      yenile();
    } catch(e) { setHata(e.message); }
  };

  const sigortaDuzenle = (s) => {
    setAktifTab('sigorta');
    setSDuzenleId(s.policeNo);
    setSForm({ aracId: s.arac?.aracId||'', policeNo: s.policeNo, sigortaSirketi: s.sigortaSirketi||'', baslangicTarihi: s.baslangicTarihi, bitisTarihi: s.bitisTarihi, primTutari: s.primTutari||0 });
    window.scrollTo({top:0, behavior:'smooth'});
  };

  const muayeneDuzenle = (m) => {
    setAktifTab('muayene');
    setMDuzenleId(m.muayeneId);
    setMForm({ aracId: m.arac?.aracId||'', muayeneIstasyonu: m.muayeneIstasyonu||'', muayeneTarihi: m.muayeneTarihi, sonrakiMuayeneTarihi: m.sonrakiMuayeneTarihi, gectiMi: m.gectiMi, notlar: m.notlar||'' });
    window.scrollTo({top:0, behavior:'smooth'});
  };

  return (
    <>
      <TopBar baslik="Sigorta / Muayene Takibi"
              aciklama="Yeni kayıt ekle ve yaklaşan/süresi dolmuş bildirimleri takip et" />
      <div className="p-8 space-y-6">
        <Uyari tip="hata" mesaj={hata} onKapat={() => setHata(null)} />
        <Uyari tip="basari" mesaj={basari} onKapat={() => setBasari(null)} />

        {/* Yeni Kayıt Formları */}
        <Kart baslik={aktifTab === 'sigorta' && sDuzenleId ? "Sigorta Düzenle" : aktifTab === 'muayene' && mDuzenleId ? "Muayene Düzenle" : "Yeni Kayıt Ekle"}>
          <div className="flex gap-4 mb-6 border-b border-aogs-kenar pb-2">
            <button type="button" onClick={() => setAktifTab('sigorta')} 
                    className={`font-medium pb-2 -mb-[9px] transition-colors ${aktifTab === 'sigorta' ? 'text-aogs-birincil border-b-2 border-aogs-birincil' : 'text-aogs-metin-hafif hover:text-white'}`}>
              Sigorta Ekle
            </button>
            <button type="button" onClick={() => setAktifTab('muayene')} 
                    className={`font-medium pb-2 -mb-[9px] transition-colors ${aktifTab === 'muayene' ? 'text-aogs-birincil border-b-2 border-aogs-birincil' : 'text-aogs-metin-hafif hover:text-white'}`}>
              Muayene Ekle
            </button>
          </div>

          {aktifTab === 'sigorta' && (
            <form onSubmit={sigortaKaydet} className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Select
                etiket="Araç *"
                secenekler={[{ deger: '', etiket: '— Araç seçin —' },
                  ...araclar.map(a => ({ deger: a.aracId, etiket: `${a.plakaNo} — ${a.marka} ${a.model}` }))]}
                value={sForm.aracId}
                onChange={e => setSForm({ ...sForm, aracId: e.target.value })}
                required
              />
              <Input etiket="Poliçe No *" value={sForm.policeNo} onChange={e => setSForm({...sForm, policeNo: e.target.value})} required />
              <Input etiket="Sigorta Şirketi" value={sForm.sigortaSirketi} onChange={e => setSForm({...sForm, sigortaSirketi: e.target.value})} />
              <Input etiket="Başlangıç Tarihi *" type="date" value={sForm.baslangicTarihi} onChange={e => setSForm({...sForm, baslangicTarihi: e.target.value})} required />
              <Input etiket="Bitiş Tarihi *" type="date" value={sForm.bitisTarihi} onChange={e => setSForm({...sForm, bitisTarihi: e.target.value})} required />
              <Input etiket="Prim Tutarı (₺)" type="number" min="0" value={sForm.primTutari} onChange={e => setSForm({...sForm, primTutari: e.target.value})} />
              <div className="md:col-span-3 flex justify-end gap-2 mt-2">
                {sDuzenleId && <Buton type="button" tip="ikincil" onClick={() => { setSDuzenleId(null); setSForm(bosSForm); }}>İptal</Buton>}
                <Buton type="submit">{sDuzenleId ? "Güncelle" : "Sigortayı Kaydet"}</Buton>
              </div>
            </form>
          )}

          {aktifTab === 'muayene' && (
            <form onSubmit={muayeneKaydet} className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Select
                etiket="Araç *"
                secenekler={[{ deger: '', etiket: '— Araç seçin —' },
                  ...araclar.map(a => ({ deger: a.aracId, etiket: `${a.plakaNo} — ${a.marka} ${a.model}` }))]}
                value={mForm.aracId}
                onChange={e => setMForm({ ...mForm, aracId: e.target.value })}
                required
              />
              <Input etiket="Muayene İstasyonu" value={mForm.muayeneIstasyonu} onChange={e => setMForm({...mForm, muayeneIstasyonu: e.target.value})} />
              <Input etiket="Muayene Tarihi *" type="date" value={mForm.muayeneTarihi} onChange={e => setMForm({...mForm, muayeneTarihi: e.target.value})} required />
              <Input etiket="Sonraki Muayene Tarihi *" type="date" value={mForm.sonrakiMuayeneTarihi} onChange={e => setMForm({...mForm, sonrakiMuayeneTarihi: e.target.value})} required />
              <Input etiket="Notlar" value={mForm.notlar} onChange={e => setMForm({...mForm, notlar: e.target.value})} />
              <label className="flex items-center gap-2 mt-6 cursor-pointer">
                <input type="checkbox" checked={mForm.gectiMi} onChange={e => setMForm({...mForm, gectiMi: e.target.checked})} className="w-4 h-4 accent-aogs-birincil" />
                <span className="text-sm font-medium">Muayeneden Geçti</span>
              </label>
              <div className="md:col-span-3 flex justify-end gap-2 mt-2">
                {mDuzenleId && <Buton type="button" tip="ikincil" onClick={() => { setMDuzenleId(null); setMForm(bosMForm); }}>İptal</Buton>}
                <Buton type="submit">{mDuzenleId ? "Güncelle" : "Muayeneyi Kaydet"}</Buton>
              </div>
            </form>
          )}
        </Kart>

        {/* Dashboard Kartları */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Kart baslik="🔔 Yaklaşan Sigortalar (30 gün)">
          <Liste
            kayitlar={veri?.yaklasanSigortalar ?? []}
            renderEt={(s) => (
              <div className="flex justify-between items-center w-full">
                <div>
                  <div className="font-medium">{s.policeNo} <span className="text-xs text-aogs-metin-hafif">— {s.sigortaSirketi}</span></div>
                  <div className="text-xs">Bitiş: {s.bitisTarihi} ({s.kalanGun} gün)</div>
                </div>
                <div className="flex gap-2">
                  <button onClick={() => sigortaDuzenle(s)} className="text-xs text-aogs-birincil hover:underline">Düzenle</button>
                  <button onClick={() => sigortaSil(s.policeNo)} className="text-xs text-aogs-uyari hover:underline">Sil</button>
                </div>
              </div>
            )}
            bos="Yaklaşan sigorta yok."
            renkVurgusu="birincil"
          />
        </Kart>

        <Kart baslik="🚦 Yaklaşan Muayeneler (30 gün)">
          <Liste
            kayitlar={veri?.yaklasanMuayeneler ?? []}
            renderEt={(m) => (
              <div className="flex justify-between items-center w-full">
                <div>
                  <div className="font-medium">Araç {m.arac?.plakaNo}</div>
                  <div className="text-xs">Tarih: {m.sonrakiMuayeneTarihi} ({m.kalanGun} gün)</div>
                </div>
                <div className="flex gap-2">
                  <button onClick={() => muayeneDuzenle(m)} className="text-xs text-aogs-birincil hover:underline">Düzenle</button>
                  <button onClick={() => muayeneSil(m.muayeneId)} className="text-xs text-aogs-uyari hover:underline">Sil</button>
                </div>
              </div>
            )}
            bos="Yaklaşan muayene yok."
            renkVurgusu="birincil"
          />
        </Kart>

        <Kart baslik="⚠️ Süresi Dolmuş Sigortalar">
          <Liste
            kayitlar={veri?.suresiDolmusSigortalar ?? []}
            renderEt={(s) => (
              <div className="flex justify-between items-center w-full">
                <div>
                  <div className="font-medium">{s.policeNo} — {s.sigortaSirketi}</div>
                  <div className="text-xs">Bitti: {s.bitisTarihi}</div>
                </div>
                <div className="flex gap-2">
                  <button onClick={() => sigortaDuzenle(s)} className="text-xs text-aogs-birincil hover:underline">Düzenle</button>
                  <button onClick={() => sigortaSil(s.policeNo)} className="text-xs text-aogs-uyari hover:underline">Sil</button>
                </div>
              </div>
            )}
            bos="Süresi dolmuş sigorta yok."
            renkVurgusu="uyari"
          />
        </Kart>

        <Kart baslik="⚠️ Süresi Dolmuş Muayeneler">
          <Liste
            kayitlar={veri?.suresiDolmusMuayeneler ?? []}
            renderEt={(m) => (
              <div className="flex justify-between items-center w-full">
                <div>
                  <div className="font-medium">Araç {m.arac?.plakaNo}</div>
                  <div className="text-xs">Son tarih: {m.sonrakiMuayeneTarihi}</div>
                </div>
                <div className="flex gap-2">
                  <button onClick={() => muayeneDuzenle(m)} className="text-xs text-aogs-birincil hover:underline">Düzenle</button>
                  <button onClick={() => muayeneSil(m.muayeneId)} className="text-xs text-aogs-uyari hover:underline">Sil</button>
                </div>
              </div>
            )}
            bos="Süresi dolmuş muayene yok."
            renkVurgusu="uyari"
          />
        </Kart>
        </div>
      </div>
    </>
  );
}

function Liste({ kayitlar, renderEt, bos, renkVurgusu }) {
  if (!kayitlar.length) {
    return <p className="text-sm text-aogs-metin-hafif">{bos}</p>;
  }
  const renk = renkVurgusu === 'uyari' ? 'border-aogs-uyari' : 'border-aogs-birincil';
  return (
    <ul className="space-y-2">
      {kayitlar.map((k, i) => (
        <li key={i} className={`border-l-4 ${renk} pl-3 py-2 bg-aogs-arkaplan/40 rounded-r`}>
          {renderEt(k)}
        </li>
      ))}
    </ul>
  );
}
