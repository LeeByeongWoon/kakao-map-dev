/*!

=========================================================
* Paper Dashboard PRO React - v1.3.0
=========================================================

* Product Page: https://www.creative-tim.com/product/paper-dashboard-pro-react
* Copyright 2021 Creative Tim (https://www.creative-tim.com)

* Coded by Creative Tim

=========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

*/
import React from "react";
import { useSelector } from "react-redux";
// javascript plugin used to create scrollbars on windows
import PerfectScrollbar from "perfect-scrollbar";
import { Route, Switch, useLocation } from "react-router-dom";

import AdminNavbar from "components/Navbars/AdminNavbar.js";
import Footer from "components/Footer/Footer.js";
import Sidebar from "components/Sidebar/Sidebar.js";
// import FixedPlugin from "components/FixedPlugin/FixedPlugin.js";

import routes from "routes.js";

import { css } from "@emotion/react";
import ClockLoader from "react-spinners/ClockLoader";


var ps;

const override = css`
  display: block;
  margin: 0 auto;
  border-color: red;
  height: 75px;
`;

function Admin(props) {
  const {
      indicator
  } = useSelector( state => ({
      indicator: state.indicator
  }));

  const location = useLocation();
  const [backgroundColor, setBackgroundColor] = React.useState("black");
  const [activeColor, setActiveColor] = React.useState("info");
  const [sidebarMini, setSidebarMini] = React.useState(false);
  const mainPanel = React.useRef();
  React.useEffect(() => {
    if (navigator.platform.indexOf("Win") > -1) {
      document.documentElement.className += " perfect-scrollbar-on";
      document.documentElement.classList.remove("perfect-scrollbar-off");
      ps = new PerfectScrollbar(mainPanel.current);
    }
    return function cleanup() {
      if (navigator.platform.indexOf("Win") > -1) {
        ps.destroy();
        document.documentElement.className += " perfect-scrollbar-off";
        document.documentElement.classList.remove("perfect-scrollbar-on");
      }
    };
  });
  React.useEffect(() => {
    document.documentElement.scrollTop = 0;
    document.scrollingElement.scrollTop = 0;
    mainPanel.current.scrollTop = 0;
  }, [location]);
  const getRoutes = (routes) => {
    return routes.map((prop, key) => {
      if (prop.collapse) {
        return getRoutes(prop.views);
      }
      if (prop.layout === "/admin") {
        return (
          <Route
            path={prop.layout + prop.path}
            component={prop.component}
            key={key}
          />
        );
      } else {
        return null;
      }
    });
  };
  const handleActiveClick = (color) => {
    setActiveColor(color);
  };
  const handleBgClick = (color) => {
    setBackgroundColor(color);
  };
  const handleMiniClick = () => {
    if (document.body.classList.contains("sidebar-mini")) {
      setSidebarMini(false);
    } else {
      setSidebarMini(true);
    }
    document.body.classList.toggle("sidebar-mini");
  };
  return (
    <div className="wrapper">
        <Sidebar
          {...props}
          routes={routes}
          bgColor={backgroundColor}
          activeColor={activeColor}
        />
        <div className="main-panel" ref={mainPanel}>
          <AdminNavbar {...props} handleMiniClick={handleMiniClick} />
          <Switch>{getRoutes(routes)}</Switch>
          {
            // we don't want the Footer to be rendered on full screen maps page
            props.location.pathname.indexOf("full-screen-map") !== -1 ? null : (
              <Footer fluid />
            )
          }
        </div>
        {/* <FixedPlugin
          bgColor={backgroundColor}
          activeColor={activeColor}
          sidebarMini={sidebarMini}
          handleActiveClick={handleActiveClick}
          handleBgClick={handleBgClick}
          handleMiniClick={handleMiniClick}
        /> */}
        {
          indicator
          ?
            <div
              style={{
                zIndex: 9999,
                position: "fixed",
                top: 0,
                left: 0,
                width: "100%",
                height: "100%",
                backgroundColor: "#F8F8F8AD"
              }}
              >
                <div
                  className="sweet-loading"
                  style={{
                    width: "100%",
                    height: "45%",
                  }}
                  />
                <div
                  className="sweet-loading"
                  style={{
                    width: "100%",
                    height: "55%",
                  }}>
                    <ClockLoader color="#36D7B7" loading={indicator} css={override} size={75} />
                </div>
            </div>
          :
            ""
        }
        
    </div>
  );
}

export default Admin;
