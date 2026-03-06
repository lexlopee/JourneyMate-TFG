export const HotelCard = ({ hotel }: { hotel: any }) => (
  <div className="hotel-card">
    <img src={hotel.image_url} alt={hotel.name} />
    <h3>{hotel.name}</h3>
    <p>Precio: {hotel.price}€</p>
    <p>Puntuación: {hotel.review_score}</p>
    <button>Ver detalles</button>
  </div>
);