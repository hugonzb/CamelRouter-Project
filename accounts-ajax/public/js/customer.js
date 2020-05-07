"use strict";

let module = angular.module('Customers', ['ngResource']);

module.factory('getCustomers', function ($resource) {
    return $resource('http://localhost:8086/api/accounts');
});

module.factory('createCustomer', function ($resource) {
    return $resource('http://localhost:8086/api/accounts', null, {update: {method: 'POST'}});
});

module.factory('createCustomerJetty', function ($resource) {
    return $resource('http://localhost:9000/createaccount', null, {update: {method: 'POST'}});
});

module.controller('CustomerController', function (getCustomers, createCustomerJetty) {
    let ctrl = this;
    ctrl.customers = getCustomers.query();
    this.createAccount = function (customer) {
        createCustomerJetty.save({}, customer, function() {
            window.location.reload();
        });
    };
    this.reloadCustomers = function () {
       ctrl.customers = getCustomers.query(); 
    };
});