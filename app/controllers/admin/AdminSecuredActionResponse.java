package controllers.admin;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Html;
import securesocial.core.java.SecureSocial;
import securesocial.core.java.SecuredActionResponses;

/**
 * Created by mgiles on 8/27/14.
 */
public class AdminSecuredActionResponse extends Controller implements SecuredActionResponses {
    @Override
    public Html notAuthorizedPage(Http.Context ctx) {
        return views.html.auth.notAuthorized.render(ctx._requestHeader(), ctx.lang(), SecureSocial.env());
    }

    @Override
    public F.Promise<Result> notAuthenticatedResult(Http.Context ctx) {
        Http.Request req = ctx.request();
        Result result;

        if (req.accepts("text/html")) {
            ctx.flash().put("error", play.i18n.Messages.get("securesocial.loginRequired"));
            ctx.session().put(SecureSocial.ORIGINAL_URL, ctx.request().uri());
            result = redirect(SecureSocial.env().routes().loginPageUrl(ctx._requestHeader()));
        } else if (req.accepts("application/json")) {
            ObjectNode node = Json.newObject();
            node.put("error", "Credentials required");
            result = unauthorized(node);
        } else {
            result = unauthorized("Credentials required");
        }
        return F.Promise.pure(result);
    }

    @Override
    public F.Promise<Result> notAuthorizedResult(Http.Context ctx) {
        Http.Request req = ctx.request();
        Result result;

        if (req.accepts("text/html")) {
            result = forbidden(notAuthorizedPage(ctx));
        } else if (req.accepts("application/json")) {
            ObjectNode node = Json.newObject();
            node.put("error", "Not authorized");
            result = forbidden(node);
        } else {
            result = forbidden("Not authorized");
        }

        return F.Promise.pure(result);
    }
}
