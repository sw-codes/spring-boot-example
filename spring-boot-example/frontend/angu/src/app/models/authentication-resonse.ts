import {CustomerDTO} from "./customer-dto";

export interface AuthenticationResonse {
  token?: string;
  customerDTO: CustomerDTO;
}
