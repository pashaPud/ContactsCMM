//
//  ComposeView.swift
//  iosContactsMP
//
//  Created by Павел on 19.10.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import shared
import Foundation
import SwiftUI

struct ComposeView: UIViewControllerRepresentable {
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
    }
    
    func makeUIViewController(context: Context) -> some UIViewController {
        MainViewControllerKt.MainViewController()
    }
}
