class CustomerRepository {
    def findBy(id) {
        if (id <= 0) {
            throw new RuntimeException('cust not found')
        }
        [id: id, name: "Cust #$id"]
    }
}

def authorizer = { repository, request ->
    def id = Integer.parseInt(request['objectId'])
    repository.findBy(id)
}

def authenticator = { authorizeWithRepo, request ->
    authorizeWithRepo(request)
}

def httpRequest1 = ['objectId': '1']
def httpRequest2 = ['objectId': '2']

def repo = new CustomerRepository()
def secure = authenticator.curry(authorizer.curry(repo))
println secure(httpRequest1)
println secure(httpRequest2)
