/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app;

import java.io.IOException;
import java.util.Enumeration;

import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;

import org.apache.myfaces.portlet.MyFacesGenericPortlet;

public class MyOwnPortlet extends MyFacesGenericPortlet {
	
	private String viewPage = null;
	private String editPage = null;
	private String helpPage = null;
	
	public void init() throws UnavailableException, PortletException {
		viewPage = (String) this.getInitParameter("ViewPage");
		editPage = (String) this.getInitParameter("EditPage");
		helpPage = (String) this.getInitParameter("HelpPage");
		super.init();
	}
	
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		PortletSession session = request.getPortletSession();
		PortletMode mode = (PortletMode) session
				.getAttribute("CurrentPortletMode");

		if (mode == null) {
			mode = request.getPortletMode();
		}

		if (mode != request.getPortletMode()) {
			request.setAttribute("isPortletModeChanged", Boolean.TRUE);
		} else {
			request.setAttribute("isPortletModeChanged", Boolean.FALSE);
		}

		session.setAttribute("CurrentPortletMode", mode);
		super.render(request, response);
	}
	
	protected void setDefaultView() throws UnavailableException {
        this.defaultView = getPortletConfig().getInitParameter(DEFAULT_VIEW);
        if (defaultView == null) {
        	this.defaultView = this.viewPage;
        }
        if (defaultView == null) {
            String msg = "Fatal2: must specify a JSF view id as the default view in portlet.xml";
            throw new UnavailableException(msg);
        }
    }

	protected void doEdit(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		Boolean isPortletModeChanged = (Boolean) request.getAttribute("isPortletModeChanged");
		if (isPortletModeChanged.booleanValue()) {
			setPortletRequestFlag(request);
			nonFacesRequest(request, response, editPage);
			return;
		}

		facesRender(request, response);
	}

	protected void doHelp(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		Boolean isPortletModeChanged = (Boolean) request.getAttribute("isPortletModeChanged");
		if (isPortletModeChanged.booleanValue()) {
			setPortletRequestFlag(request);
			nonFacesRequest(request, response, helpPage);
			return;
		}

		facesRender(request, response);
	}
}
