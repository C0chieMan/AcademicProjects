import { Product } from "./product";

export interface User {
  _id: string;
  username: string;
  password: string;
  imageProfile: string;
  wishlist: string[];
  library: { productID: string, quantity: number, purchasedAt: Date }[];
}
