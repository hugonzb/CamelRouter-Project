"use strict";

let module = angular.module('Customer', ['ngResource']);

module.factory('getCustomers', function ($resource) {
    return $resource('http://localhost:7081/api/customer');
});

module.factory('createCustomer', function ($resource) {
    return $resource('http://localhost:7081/api/customer');
});

module.controller('CustomerController', function (getCustomers, createCustomer) {
    this.customers - getCustomers.query();
    this.createNewCustomer = function (customer) {
        createCustomer.save(null, customer, function() {
            this.customers = getCustomers.query();
        });
    };
});