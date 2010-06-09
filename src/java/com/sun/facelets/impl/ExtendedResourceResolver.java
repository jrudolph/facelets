package com.sun.facelets.impl;

import java.io.IOException;
import java.net.URL;

public interface ExtendedResourceResolver extends ResourceResolver {
    public URL resolveRelativeUrl(URL base, String path) throws IOException;
}
