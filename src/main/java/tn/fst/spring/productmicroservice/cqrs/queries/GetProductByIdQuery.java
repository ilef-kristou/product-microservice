package tn.fst.spring.productmicroservice.cqrs.queries;

public class GetProductByIdQuery {
    private String id; // ou UUID id;

    public GetProductByIdQuery(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
