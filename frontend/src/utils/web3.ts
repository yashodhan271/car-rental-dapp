import { InjectedConnector } from '@web3-react/injected-connector';
import { Contract } from '@ethersproject/contracts';
import { Web3Provider } from '@ethersproject/providers';

export const injectedConnector = new InjectedConnector({
  supportedChainIds: [1, 3, 4, 5, 42, 1337], // Mainnet, Ropsten, Rinkeby, Goerli, Kovan, Local
});

export const getContract = (address: string, abi: any, library: Web3Provider, account?: string) => {
  if (!address || !abi || !library) return null;
  try {
    return new Contract(
      address,
      abi,
      account ? library.getSigner(account).connectUnchecked() : library
    );
  } catch (error) {
    console.error('Failed to get contract', error);
    return null;
  }
};

export const shortenAddress = (address: string) => {
  return `${address.substring(0, 6)}...${address.substring(address.length - 4)}`;
};
