# 🚗 Decentralized Car Rental System

<div align="center">

![Car Rental Banner](https://img.shields.io/badge/🚗-Decentralized%20Car%20Rental-blue?style=for-the-badge)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://reactjs.org/)
[![Ethereum](https://img.shields.io/badge/Ethereum-3C3C3D?style=for-the-badge&logo=ethereum&logoColor=white)](https://ethereum.org/)
[![Web3.js](https://img.shields.io/badge/Web3.js-F16822?style=for-the-badge&logo=web3.js&logoColor=white)](https://web3js.readthedocs.io/)
[![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/)

A modern, secure, and decentralized car rental platform built on Ethereum blockchain.

</div>

## 🌟 Features

- **🔐 Secure Authentication**
  - Web3 wallet integration (MetaMask)
  - JWT-based session management
  - Role-based access control (RBAC)

- **🚙 Car Management**
  - Register cars on blockchain
  - Upload car details and images to IPFS
  - Real-time GPS tracking
  - Comprehensive car search and filters

- **💰 Smart Payments**
  - Ethereum-based transactions
  - Automatic payment processing
  - Complete payment history
  - Rental deposit management

- **📱 User Experience**
  - Responsive Material-UI design
  - Real-time notifications
  - Interactive car location tracking
  - Intuitive rental management

## 🏗️ Architecture

```mermaid
graph TD
    A[Frontend - React] -->|API Calls| B[Backend - Spring Boot]
    B -->|Store Data| C[MongoDB]
    B -->|Smart Contract Interaction| D[Ethereum Blockchain]
    A -->|Web3| D
    B -->|Store Images| E[IPFS]
```

## 🛠️ Tech Stack

### Backend
- **Framework:** Spring Boot 3.x
- **Security:** Spring Security, JWT
- **Blockchain:** Web3j, Ethereum
- **Database:** MongoDB
- **Build Tool:** Maven

### Frontend
- **Framework:** React 18
- **UI Library:** Material-UI v5
- **Web3:** Web3-React
- **State Management:** React Context
- **HTTP Client:** Axios

### Blockchain
- **Network:** Ethereum
- **Smart Contracts:** Solidity
- **Development:** Truffle/Hardhat
- **Client:** Web3j

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Node.js 16+
- MongoDB
- Ethereum Wallet (MetaMask)
- Ganache (for local blockchain)

### Backend Setup
```bash
# Clone the repository
git clone https://github.com/yourusername/car-rental-dapp.git

# Navigate to backend directory
cd car-rental-dapp

# Install dependencies
mvn clean install

# Run the application
mvn spring-boot:run
```

### Frontend Setup
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start the development server
npm start
```

### Smart Contract Deployment
```bash
# Deploy contracts
truffle migrate --network development

# Update contract addresses
Update application.yml with new contract addresses
```

## 🔒 Security Features

- **Wallet Authentication**
  - Nonce-based signature verification
  - Secure session management

- **Data Protection**
  - Encrypted data storage
  - Secure API endpoints
  - CORS protection

- **Smart Contract Security**
  - Access control modifiers
  - Reentrancy protection
  - Emergency pause functionality

## 📱 Screenshots

<div align="center">
<details>
<summary>Click to view screenshots</summary>

### Home Page
![Home Page](path/to/homepage.png)

### Car Listing
![Car Listing](path/to/car-listing.png)

### Rental Dashboard
![Dashboard](path/to/dashboard.png)

</details>
</div>

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- OpenZeppelin for smart contract libraries
- Material-UI team for the amazing UI components
- Web3j community for Ethereum integration tools

## 📞 Contact

For questions and support, please open an issue or contact us at:
- 📧 Email: support@carrentaldapp.com
- 💬 Discord: [Join our server](https://discord.gg/carrentaldapp)

---

<div align="center">

Made with ❤️ by the Car Rental DApp Team

[![Follow us on Twitter](https://img.shields.io/twitter/follow/carrentaldapp?style=social)](https://twitter.com/carrentaldapp)
[![Star on GitHub](https://img.shields.io/github/stars/yourusername/car-rental-dapp?style=social)](https://github.com/yourusername/car-rental-dapp)

</div>
