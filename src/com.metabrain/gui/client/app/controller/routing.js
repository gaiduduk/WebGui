app.config(function($routeProvider) {
    $routeProvider
        .when("/", {
            templateUrl : "app/template/start.html",
            controller: "start"
        })/*
        .when("/enter_space", {
            templateUrl : "app/template/enter_space.html",
            controller: "enter_space"
        })*/;
});