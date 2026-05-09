/**
 * Üst başlık çubuğu.
 * Sayfa başlığını ve isteğe bağlı sağ aksiyon butonunu gösterir.
 */
export default function TopBar({ baslik, aciklama, aksiyon }) {
  return (
    <div className="bg-white border-b border-aogs-kenar px-8 py-4 flex items-center justify-between sticky top-0 z-10">
      <div>
        <h1 className="text-2xl font-bold text-aogs-metin">{baslik}</h1>
        {aciklama && (
          <p className="text-sm text-aogs-metin-hafif mt-1">{aciklama}</p>
        )}
      </div>
      {aksiyon && <div>{aksiyon}</div>}
    </div>
  );
}
