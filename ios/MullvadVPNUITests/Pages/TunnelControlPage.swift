//
//  TunnelControlPage.swift
//  MullvadVPNUITests
//
//  Created by Niklas Berglund on 2024-01-11.
//  Copyright © 2024 Mullvad VPN AB. All rights reserved.
//

import Foundation
import XCTest

class TunnelControlPage: Page {
    @discardableResult override init(_ app: XCUIApplication) {
        super.init(app)

        self.pageAccessibilityIdentifier = .tunnelControlView
        waitForPageToBeShown()
    }

    @discardableResult func tapSelectLocationButton() -> Self {
        app.buttons[AccessibilityIdentifier.selectLocationButton].tap()
        return self
    }

    @discardableResult func tapSecureConnectionButton() -> Self {
        app.buttons[AccessibilityIdentifier.secureConnectionButton].tap()
        return self
    }

    @discardableResult func waitForSecureConnectionLabel() -> Self {
        _ = app.staticTexts[AccessibilityIdentifier.connectionStatusLabel]
            .waitForExistence(timeout: BaseUITestCase.defaultTimeout)
        return self
    }
}
