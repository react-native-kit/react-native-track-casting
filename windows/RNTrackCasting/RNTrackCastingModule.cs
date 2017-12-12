using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Track.Casting.RNTrackCasting
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNTrackCastingModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNTrackCastingModule"/>.
        /// </summary>
        internal RNTrackCastingModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNTrackCasting";
            }
        }
    }
}
