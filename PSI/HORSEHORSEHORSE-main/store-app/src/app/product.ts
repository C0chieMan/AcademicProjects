export class Product {
  _id: string;
  tipo: string;
  name: string;
  description: string;
  imageUrl: string;
  price: number;
  rate: string;
  plataform: string;
  idioma: string;
  youtubeURL: string;
  otherImages: string[];

  constructor(id: string, name: string, tipo: string, description: string, price: number, imageUrl: string, rate: string, plataform: string, idioma: string, youtubeURL: string, otherImages: string[]) {
    this._id = id;
    this.tipo = tipo;
    this.name = name;
    this.description = description;
    this.price = price;
    this.imageUrl = imageUrl;
    this.rate = rate;
    this.plataform = plataform;
    this.idioma = idioma;
    this.youtubeURL = youtubeURL;
    this.otherImages = otherImages;
  }
}
