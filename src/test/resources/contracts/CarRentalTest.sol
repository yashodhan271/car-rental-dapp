// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "truffle/Assert.sol";
import "truffle/DeployedAddresses.sol";
import "../../main/resources/contracts/CarRental.sol";

contract CarRentalTest {
    CarRental carRental;
    address owner = address(this);

    // Set up the contract instance before each test
    function beforeEach() public {
        carRental = new CarRental();
    }

    function testRegisterCar() public {
        string memory vin = "VIN123";
        uint256 pricePerDay = 1 ether;
        bool success = carRental.registerCar(vin, pricePerDay);
        
        Assert.equal(success, true, "Car registration should succeed");
        
        (address carOwner, uint256 price, bool isAvailable, bool exists) = carRental.getCar(vin);
        
        Assert.equal(carOwner, owner, "Car owner should be test contract");
        Assert.equal(price, pricePerDay, "Price per day should match");
        Assert.equal(isAvailable, true, "Car should be available");
        Assert.equal(exists, true, "Car should exist");
    }

    function testRentCar() public {
        string memory vin = "VIN123";
        uint256 pricePerDay = 1 ether;
        uint256 durationInDays = 3;
        
        // Register car first
        carRental.registerCar(vin, pricePerDay);
        
        // Calculate total price
        uint256 totalPrice = pricePerDay * durationInDays;
        
        // Rent the car
        bool success = carRental.rentCar{value: totalPrice}(vin, durationInDays);
        Assert.equal(success, true, "Car rental should succeed");
        
        // Check car status
        (,, bool isAvailable,) = carRental.getCar(vin);
        Assert.equal(isAvailable, false, "Car should not be available after rental");
        
        // Check rental details
        (address renter, uint256 endTime, bool active) = carRental.getRental(vin);
        Assert.equal(renter, owner, "Renter should be test contract");
        Assert.equal(active, true, "Rental should be active");
    }

    function testCompleteRental() public {
        string memory vin = "VIN123";
        uint256 pricePerDay = 1 ether;
        uint256 durationInDays = 3;
        
        // Setup: Register and rent car
        carRental.registerCar(vin, pricePerDay);
        carRental.rentCar{value: pricePerDay * durationInDays}(vin, durationInDays);
        
        // Complete the rental
        bool success = carRental.completeRental(vin);
        Assert.equal(success, true, "Rental completion should succeed");
        
        // Check car status
        (,, bool isAvailable,) = carRental.getCar(vin);
        Assert.equal(isAvailable, true, "Car should be available after rental completion");
        
        // Check rental status
        (,, bool active) = carRental.getRental(vin);
        Assert.equal(active, false, "Rental should not be active");
    }

    function testFailRentUnavailableCar() public {
        string memory vin = "VIN123";
        uint256 pricePerDay = 1 ether;
        uint256 durationInDays = 3;
        
        // Setup: Register and rent car by first renter
        carRental.registerCar(vin, pricePerDay);
        carRental.rentCar{value: pricePerDay * durationInDays}(vin, durationInDays);
        
        // Try to rent already rented car
        bool success = carRental.rentCar{value: pricePerDay * durationInDays}(vin, durationInDays);
        Assert.equal(success, false, "Should not be able to rent unavailable car");
    }

    function testFailInsufficientPayment() public {
        string memory vin = "VIN123";
        uint256 pricePerDay = 1 ether;
        uint256 durationInDays = 3;
        
        // Register car
        carRental.registerCar(vin, pricePerDay);
        
        // Try to rent with insufficient payment
        uint256 insufficientPayment = (pricePerDay * durationInDays) - 1;
        bool success = carRental.rentCar{value: insufficientPayment}(vin, durationInDays);
        Assert.equal(success, false, "Should not be able to rent with insufficient payment");
    }

    // Receive function to accept ETH payments
    receive() external payable {}
}
