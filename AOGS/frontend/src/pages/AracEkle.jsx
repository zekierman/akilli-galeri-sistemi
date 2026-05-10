import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import TopBar from '../components/layout/TopBar';
import { Kart, Buton, Input, Select, Uyari } from '../components/ui/Kontroller';
import { aracApi } from '../api/client';

const ARAC_TIPLERI = [
  { deger: 'OTOMOBIL',   etiket: 'Otomobil' },
  { deger: 'SUV',        etiket: 'SUV' },
  { deger: 'TICARI',     etiket: 'Ticari' },
  { deger: 'MOTOSIKLET', etiket: 'Motosiklet' },
];
const YAKITLAR  = ['BENZIN', 'DIZEL', 'LPG', 'ELEKTRIK', 'HIBRIT'];
const VITESLER  = ['MANUEL', 'OTOMATIK', 'YARI_OTOMATIK'];
const DURUMLAR  = ['SATISTA', 'SATILDI', 'REZERVE', 'SERVISTE'];

export default function AracEkle() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [tip, setTip] = useState('OTOMOBIL');
  const [hata, setHata] = useState(null);
  const [basari, setBasari] = useState(null);

  // Tüm araçların ortak alanları
  const [form, setForm] = useState({
    marka: '', model: '', yil: 2020, km: 0,
    yakitTipi: 'BENZIN', vitesTipi: 'MANUEL',
    renk: '', plakaNo: '', sasiNo: '',
    alisFiyati: 0, satisFiyati: 0,
    durum: 'SATISTA',
    // Tip-spesifik
    kasaTipi: 'Sedan', kapiSayisi: 4, sunroof: false,
    dortCeker: false, bagajHacmi: 500, offRoad: false,
    tasimaKapasitesi: 1000, ticariTip: 'Panel Van', frigorifik: false,
    motorHacmi: 250, motosikletTipi: 'Sport', abs: true,
  });

  const parseNumber = (value) => {
    if (value == null || value === '') return 0;
    const normalized = String(value).replace(/\./g, '').replace(/,/g, '.');
    return parseFloat(normalized) || 0;
  };

  useEffect(() => {
    if (id) {
      aracApi.bul(id).then(a => {
        setTip(a.aracTipi || 'OTOMOBIL');
        setForm({
          marka: a.marka, model: a.model, yil: a.yil, km: a.km,
          yakitTipi: a.yakitTipi, vitesTipi: a.vitesTipi,
          renk: a.renk || '', plakaNo: a.plakaNo, sasiNo: a.sasiNo || '',
          alisFiyati: a.alisFiyati, satisFiyati: a.satisFiyati,
          durum: a.durum,
          kasaTipi: a.kasaTipi || 'Sedan', kapiSayisi: a.kapiSayisi || 4, sunroof: a.sunroof || false,
          dortCeker: a.dortCeker || false, bagajHacmi: a.bagajHacmi || 500, offRoad: a.offRoad || false,
          tasimaKapasitesi: a.tasimaKapasitesi || 1000, ticariTip: a.ticariTip || 'Panel Van', frigorifik: a.frigorifik || false,
          motorHacmi: a.motorHacmi || 250, motosikletTipi: a.motosikletTipi || 'Sport', abs: a.abs || true,
        });
      }).catch(e => setHata('Araç bilgileri alınamadı: ' + e.message));
    }
  }, [id]);

  const guncelle = (alan) => (e) => {
    const v = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setForm({ ...form, [alan]: v });
  };

  const submit = async (e) => {
    e.preventDefault();
    setHata(null); setBasari(null);

    // Ortak alanlar — sayısal alanları parse et
    const ortak = {
      marka: form.marka, model: form.model,
      yil: parseInt(form.yil),
      km: parseNumber(form.km),
      yakitTipi: form.yakitTipi,
      vitesTipi: form.vitesTipi,
      renk: form.renk, plakaNo: form.plakaNo, sasiNo: form.sasiNo,
      alisFiyati: parseNumber(form.alisFiyati),
      satisFiyati: parseNumber(form.satisFiyati),
      durum: form.durum,
    };

    // JPA discriminator'ı için tipe göre doğru property eklenir
    let payload;
    switch (tip) {
      case 'OTOMOBIL':
        payload = { '@type': 'OTOMOBIL', ...ortak,
          kasaTipi: form.kasaTipi, kapiSayisi: parseInt(form.kapiSayisi), sunroof: form.sunroof };
        break;
      case 'SUV':
        payload = { '@type': 'SUV', ...ortak,
          dortCeker: form.dortCeker, bagajHacmi: parseFloat(form.bagajHacmi), offRoad: form.offRoad };
        break;
      case 'TICARI':
        payload = { '@type': 'TICARI', ...ortak,
          tasimaKapasitesi: parseFloat(form.tasimaKapasitesi),
          ticariTip: form.ticariTip, frigorifik: form.frigorifik };
        break;
      case 'MOTOSIKLET':
        payload = { '@type': 'MOTOSIKLET', ...ortak,
          motorHacmi: parseInt(form.motorHacmi),
          motosikletTipi: form.motosikletTipi, abs: form.abs };
        break;
      default:
        return;
    }

    try {
      if (id) {
        await aracApi.guncelle(id, payload);
        setBasari('Araç başarıyla güncellendi.');
      } else {
        await aracApi.ekle(payload);
        setBasari('Araç başarıyla eklendi.');
      }
      setTimeout(() => navigate('/araclar'), 800);
    } catch (err) {
      setHata(err.message);
    }
  };

  return (
    <>
      <TopBar baslik={id ? "Araç Düzenle" : "Araç Ekleme"} aciklama={id ? "Mevcut araç bilgilerini güncelle" : "Galeriye yeni araç kaydı oluştur"} />
      <div className="p-8 max-w-5xl">
        <Uyari tip="hata" mesaj={hata} onKapat={() => setHata(null)} />
        <Uyari tip="basari" mesaj={basari} />

        <form onSubmit={submit} className="space-y-6">
          {/* Tip seçimi */}
          <Kart baslik="Araç Tipi">
            <Select
              etiket="Tip"
              secenekler={ARAC_TIPLERI}
              value={tip}
              onChange={(e) => setTip(e.target.value)}
              disabled={!!id}
            />
          </Kart>

          {/* Ortak alanlar */}
          <Kart baslik="Genel Bilgiler">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              <Input etiket="Marka *" value={form.marka} onChange={guncelle('marka')} required />
              <Input etiket="Model *" value={form.model} onChange={guncelle('model')} required />
              <Input etiket="Yıl"     type="number" value={form.yil} onChange={guncelle('yil')} />
              <Input etiket="KM"      type="number" value={form.km}  onChange={guncelle('km')} />
              <Select etiket="Yakıt"  secenekler={YAKITLAR} value={form.yakitTipi} onChange={guncelle('yakitTipi')} />
              <Select etiket="Vites"  secenekler={VITESLER} value={form.vitesTipi} onChange={guncelle('vitesTipi')} />
              <Input etiket="Renk"    value={form.renk} onChange={guncelle('renk')} />
              <Input etiket="Plaka *" value={form.plakaNo} onChange={guncelle('plakaNo')} required />
              <Input etiket="Şasi No" value={form.sasiNo} onChange={guncelle('sasiNo')} />
              <Input etiket="Alış Fiyatı (₺)"  type="number" value={form.alisFiyati}  onChange={guncelle('alisFiyati')} />
              <Input etiket="Satış Fiyatı (₺)" type="number" value={form.satisFiyati} onChange={guncelle('satisFiyati')} />
              <Input etiket="KM"      type="number" value={form.km}  onChange={guncelle('km')} />
              <Select etiket="Durum *" secenekler={DURUMLAR} value={form.durum} onChange={guncelle('durum')} required />
            </div>
          </Kart>

          {/* Tip-spesifik alanlar */}
          <Kart baslik="Tipe Özel Bilgiler">
            {tip === 'OTOMOBIL' && (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <Input etiket="Kasa Tipi"   value={form.kasaTipi} onChange={guncelle('kasaTipi')} />
                <Input etiket="Kapı Sayısı" type="number" value={form.kapiSayisi} onChange={guncelle('kapiSayisi')} />
                <Onay etiket="Sunroof" deger={form.sunroof} onChange={guncelle('sunroof')} />
              </div>
            )}
            {tip === 'SUV' && (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <Onay etiket="4x4 Çeker" deger={form.dortCeker} onChange={guncelle('dortCeker')} />
                <Input etiket="Bagaj Hacmi (L)" type="number" value={form.bagajHacmi} onChange={guncelle('bagajHacmi')} />
                <Onay etiket="Off-Road Paketi" deger={form.offRoad} onChange={guncelle('offRoad')} />
              </div>
            )}
            {tip === 'TICARI' && (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <Input etiket="Taşıma Kapasitesi (kg)" type="number" value={form.tasimaKapasitesi} onChange={guncelle('tasimaKapasitesi')} />
                <Input etiket="Ticari Tip" value={form.ticariTip} onChange={guncelle('ticariTip')} />
                <Onay etiket="Frigorifik" deger={form.frigorifik} onChange={guncelle('frigorifik')} />
              </div>
            )}
            {tip === 'MOTOSIKLET' && (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <Input etiket="Motor Hacmi (cc)" type="number" value={form.motorHacmi} onChange={guncelle('motorHacmi')} />
                <Input etiket="Motosiklet Tipi"  value={form.motosikletTipi} onChange={guncelle('motosikletTipi')} />
                <Onay etiket="ABS" deger={form.abs} onChange={guncelle('abs')} />
              </div>
            )}
          </Kart>

          <div className="flex gap-3">
            <Buton type="submit">{id ? "Güncelle" : "Kaydet"}</Buton>
            <Buton type="button" tip="ikincil" onClick={() => navigate('/araclar')}>
              İptal
            </Buton>
          </div>
        </form>
      </div>
    </>
  );
}

function Onay({ etiket, deger, onChange }) {
  return (
    <label className="flex items-center gap-2 mt-6 cursor-pointer">
      <input type="checkbox" checked={deger} onChange={onChange} className="w-4 h-4 accent-aogs-birincil" />
      <span className="text-sm font-medium">{etiket}</span>
    </label>
  );
}
