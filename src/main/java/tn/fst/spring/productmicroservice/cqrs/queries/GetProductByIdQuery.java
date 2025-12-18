package tn.fst.spring.productmicroservice.cqrs.queries;

public class GetProductByIdQuery {
    private final Long id;  // Changé en Long

    public GetProductByIdQuery(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}