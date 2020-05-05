package resource;

import dao.SaleDAO;
import domain.ErrorMessage;
import org.jooby.Jooby;
import org.jooby.MediaType;
import org.jooby.Status;

public class CustomerSalesResource extends Jooby {

	public CustomerSalesResource(SaleDAO dao) {

		path("/api/sales/customer", () -> {

			/**
			 * Get customer sales.
			 */
			get("/:id", (req, rsp) -> {

				String customerId = req.param("id").value();

				if (dao.doesCustomerExist(customerId)) {
					rsp.send(dao.getSales(customerId));
				} else {
					rsp.status(Status.NOT_FOUND).send(new ErrorMessage("That customer does not exists"));
				}

			});

			/**
			 * Get customer sales summary.
			 */
			get("/:id/summary", (req, rsp) -> {
				String customerId = req.param("id").value();

				if (dao.doesCustomerExist(customerId)) {
					rsp.send(dao.getSummary(customerId));
				} else {
					rsp.status(Status.NOT_FOUND).send(new ErrorMessage("That customer does not exists"));
				}

			});

		}).produces(MediaType.json).consumes(MediaType.json);

	}

}
