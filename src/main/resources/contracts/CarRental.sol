// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract CarRental {
    struct Car {
        address owner;
        string vinNumber;
        string make;
        string model;
        uint256 year;
        bool isAvailable;
        uint256 rentalPrice; // per day in wei
        string ipfsDocumentHash;
    }

    struct Rental {
        address renter;
        uint256 startTime;
        uint256 endTime;
        uint256 totalAmount;
        bool isActive;
        string gpsTrackingId;
    }

    mapping(string => Car) public cars; // VIN number to Car
    mapping(string => Rental) public rentals; // VIN number to current rental
    mapping(address => string[]) public userCars; // Owner address to array of VIN numbers

    event CarRegistered(string vinNumber, address owner);
    event CarRented(string vinNumber, address renter, uint256 startTime, uint256 endTime);
    event RentalCompleted(string vinNumber, address renter);
    event OwnershipTransferred(string vinNumber, address previousOwner, address newOwner);

    modifier onlyCarOwner(string memory vinNumber) {
        require(cars[vinNumber].owner == msg.sender, "Only car owner can perform this action");
        _;
    }

    modifier carExists(string memory vinNumber) {
        require(cars[vinNumber].owner != address(0), "Car does not exist");
        _;
    }

    function registerCar(
        string memory vinNumber,
        string memory make,
        string memory model,
        uint256 year,
        uint256 rentalPrice,
        string memory ipfsDocumentHash
    ) external {
        require(cars[vinNumber].owner == address(0), "Car already registered");
        
        Car memory newCar = Car({
            owner: msg.sender,
            vinNumber: vinNumber,
            make: make,
            model: model,
            year: year,
            isAvailable: true,
            rentalPrice: rentalPrice,
            ipfsDocumentHash: ipfsDocumentHash
        });

        cars[vinNumber] = newCar;
        userCars[msg.sender].push(vinNumber);
        
        emit CarRegistered(vinNumber, msg.sender);
    }

    function rentCar(string memory vinNumber, uint256 durationInDays, string memory gpsTrackingId) 
        external 
        payable 
        carExists(vinNumber) 
    {
        Car storage car = cars[vinNumber];
        require(car.isAvailable, "Car is not available for rent");
        require(msg.value >= car.rentalPrice * durationInDays, "Insufficient payment");

        uint256 startTime = block.timestamp;
        uint256 endTime = startTime + (durationInDays * 1 days);

        Rental memory newRental = Rental({
            renter: msg.sender,
            startTime: startTime,
            endTime: endTime,
            totalAmount: msg.value,
            isActive: true,
            gpsTrackingId: gpsTrackingId
        });

        rentals[vinNumber] = newRental;
        car.isAvailable = false;

        emit CarRented(vinNumber, msg.sender, startTime, endTime);
    }

    function completeRental(string memory vinNumber) 
        external 
        carExists(vinNumber) 
    {
        Rental storage rental = rentals[vinNumber];
        require(rental.isActive, "No active rental found");
        require(msg.sender == rental.renter || msg.sender == cars[vinNumber].owner, 
                "Only renter or owner can complete rental");

        rental.isActive = false;
        cars[vinNumber].isAvailable = true;

        // Transfer rental payment to car owner
        payable(cars[vinNumber].owner).transfer(rental.totalAmount);

        emit RentalCompleted(vinNumber, rental.renter);
    }

    function transferOwnership(string memory vinNumber, address newOwner) 
        external 
        carExists(vinNumber) 
        onlyCarOwner(vinNumber) 
    {
        require(newOwner != address(0), "Invalid new owner address");
        require(!rentals[vinNumber].isActive, "Cannot transfer car with active rental");

        address previousOwner = cars[vinNumber].owner;
        cars[vinNumber].owner = newOwner;

        // Remove car from previous owner's list
        removeCarFromUserList(previousOwner, vinNumber);
        
        // Add car to new owner's list
        userCars[newOwner].push(vinNumber);

        emit OwnershipTransferred(vinNumber, previousOwner, newOwner);
    }

    function removeCarFromUserList(address owner, string memory vinNumber) internal {
        string[] storage ownerCars = userCars[owner];
        for (uint i = 0; i < ownerCars.length; i++) {
            if (keccak256(bytes(ownerCars[i])) == keccak256(bytes(vinNumber))) {
                // Move the last element to this position and pop the last element
                ownerCars[i] = ownerCars[ownerCars.length - 1];
                ownerCars.pop();
                break;
            }
        }
    }

    function getCarDetails(string memory vinNumber) 
        external 
        view 
        returns (
            address owner,
            string memory make,
            string memory model,
            uint256 year,
            bool isAvailable,
            uint256 rentalPrice,
            string memory ipfsDocumentHash
        ) 
    {
        Car memory car = cars[vinNumber];
        return (
            car.owner,
            car.make,
            car.model,
            car.year,
            car.isAvailable,
            car.rentalPrice,
            car.ipfsDocumentHash
        );
    }

    function getCurrentRental(string memory vinNumber) 
        external 
        view 
        returns (
            address renter,
            uint256 startTime,
            uint256 endTime,
            uint256 totalAmount,
            bool isActive,
            string memory gpsTrackingId
        ) 
    {
        Rental memory rental = rentals[vinNumber];
        return (
            rental.renter,
            rental.startTime,
            rental.endTime,
            rental.totalAmount,
            rental.isActive,
            rental.gpsTrackingId
        );
    }
}
